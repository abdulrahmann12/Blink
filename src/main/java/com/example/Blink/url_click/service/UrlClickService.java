package com.example.Blink.url_click.service;

import com.example.Blink.exception.UnauthorizedException;
import com.example.Blink.exception.UrlNotFoundException;
import com.example.Blink.security.AuthenticatedUserService;
import com.example.Blink.url.entity.Url;
import com.example.Blink.url.repository.UrlRepository;
import com.example.Blink.url_click.dto.UrlClickResponse;
import com.example.Blink.url_click.entity.DeviceType;
import com.example.Blink.url_click.entity.UrlClick;
import com.example.Blink.url_click.mapper.UrlClickMapper;
import com.example.Blink.url_click.model.ClickTrackingEvent;
import com.example.Blink.url_click.model.UserAgentData;
import com.example.Blink.url_click.repository.UrlClickRepository;
import com.example.Blink.user.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Service
@RequiredArgsConstructor
@Validated
@Slf4j
public class UrlClickService {

    private final UrlClickRepository urlClickRepository;
    private final UrlClickMapper urlClickMapper;
    private final UrlRepository urlRepository;
    private final AuthenticatedUserService authenticatedUserService;
    private final ApplicationEventPublisher eventPublisher;
    private final GeoIpService geoIpService;

    private static final Pattern IP_PATTERN = Pattern.compile(
            "^([0-9]{1,3}\\.){3}[0-9]{1,3}$|^([0-9a-fA-F]{0,4}:){2,7}[0-9a-fA-F]{0,4}$"
    );

    private static final Pattern BOT_PATTERN = Pattern.compile(
            "bot|crawl|spider|slurp|mediapartners|facebookexternalhit|twitterbot|linkedinbot|whatsapp|telegrambot|bingpreview|googlebot",
            Pattern.CASE_INSENSITIVE
    );

    // ========================= Click Tracking =========================

    /**
     * Extracts request metadata synchronously and publishes an async event for persistence.
     * This ensures HttpServletRequest data is read on the request thread,
     * while the DB write happens asynchronously without blocking the redirect.
     */
    public void trackClick(Url url, HttpServletRequest request) {
        String ip = extractClientIp(request);
        String userAgent = request.getHeader("User-Agent");
        String referrer = sanitizeReferrer(request.getHeader("Referer"));
        UserAgentData ua = parseUserAgent(userAgent);


        try {
            eventPublisher.publishEvent(
                    new ClickTrackingEvent(url.getUrlId(), ip, ua, referrer)
            );
        } catch (Exception e) {
            log.warn("Click tracking skipped for URL {}: {}", url.getUrlId(), e.getMessage());
        }
    }

    @Async("clickTrackingExecutor")
    @EventListener
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "urlClicks", allEntries = true),
            @CacheEvict(value = "totalClicks", allEntries = true),
            @CacheEvict(value = "clicksByDate", allEntries = true),
            @CacheEvict(value = "topCountries", allEntries = true),
            @CacheEvict(value = "topBrowsers", allEntries = true)
    })
    public void handleClickTrackingEvent(ClickTrackingEvent event) {
        try {
            Url url = urlRepository.getReferenceById(event.urlId());

            UrlClick click = UrlClick.builder()
                    .url(url)
                    .ipAddress(event.ip())
                    .browser(event.userAgentData().browser())
                    .operatingSystem(event.userAgentData().os())
                    .deviceType(event.userAgentData().deviceType())
                    .country(geoIpService.getCountry(event.ip()))
                    .referrer(event.referrer())
                    .visitedAt(Instant.now())
                    .build();

            urlClickRepository.save(click);

            urlRepository.incrementClickCount(url.getUrlId());
        } catch (Exception e) {
            log.error("Failed to track click for URL {}: {}", event.urlId(), e.getMessage());
        }
    }

    // ========================= Query =========================

    public Page<UrlClickResponse> getClicksByUrlId(UUID urlId, int page, int size) {
        Url url = urlRepository.findById(urlId)
                .orElseThrow(UrlNotFoundException::new);

        User currentUser = authenticatedUserService.getCurrentUser();
        if (!url.getUser().getUserId().equals(currentUser.getUserId())) {
            throw new UnauthorizedException();
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<UrlClick> clicks = urlClickRepository.findByUrl_UrlId(urlId, pageable);
        return clicks.map(urlClickMapper::toUrlClickResponse);
    }

    // ========================= IP Extraction =========================

    String extractClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");

        if (ip != null && !ip.isBlank()) {
            ip = ip.split(",")[0].trim();
        } else {
            ip = request.getRemoteAddr();
        }

        // Validate IP format — reject spoofed/malformed values
        if (ip != null && IP_PATTERN.matcher(ip).matches()) {
            return ip;
        }

        return "0.0.0.0";
    }

    // ========================= User-Agent Parsing =========================

    UserAgentData parseUserAgent(String userAgent) {
        if (userAgent == null || userAgent.isBlank()) {
            return new UserAgentData("Unknown", "Unknown", DeviceType.UNKNOWN);
        }

        // Bot detection — check first to avoid misclassifying bots
        if (BOT_PATTERN.matcher(userAgent).find()) {
            return new UserAgentData("Bot", "Bot", DeviceType.BOT);
        }

        String browser = "Unknown";
        String os = "Unknown";
        DeviceType deviceType = DeviceType.UNKNOWN;

        // Browser — order matters! Edge & Opera UAs contain "Chrome", so check them first
        if (userAgent.contains("Edg")) browser = "Edge";
        else if (userAgent.contains("OPR") || userAgent.contains("Opera")) browser = "Opera";
        else if (userAgent.contains("Chrome")) browser = "Chrome";
        else if (userAgent.contains("Safari")) browser = "Safari";
        else if (userAgent.contains("Firefox")) browser = "Firefox";

        // OS — iPhone and iPad both map to iOS
        if (userAgent.contains("Windows")) os = "Windows";
        else if (userAgent.contains("Mac")) os = "MacOS";
        else if (userAgent.contains("Android")) os = "Android";
        else if (userAgent.contains("iPhone") || userAgent.contains("iPad")) os = "iOS";
        else if (userAgent.contains("Linux")) os = "Linux";

        // Device type
        if (userAgent.contains("Mobi")) deviceType = DeviceType.MOBILE;
        else if (userAgent.contains("Tablet") || userAgent.contains("iPad")) deviceType = DeviceType.TABLET;
        else deviceType = DeviceType.DESKTOP;

        return new UserAgentData(browser, os, deviceType);
    }

    // ========================= Sanitization =========================

    private String sanitizeReferrer(String referrer) {
        if (referrer == null || referrer.isBlank()) {
            return null;
        }

        // Strip HTML/script tags to prevent stored XSS
        String sanitized = referrer.replaceAll("<[^>]*>", "").trim();

        if (sanitized.length() > 500) {
            sanitized = sanitized.substring(0, 500);
        }

        return sanitized;
    }

    @Cacheable(value = "totalClicks", key = "#p0")
    public Long totalClick(UUID urlId) {
        Url url = urlRepository.findById(urlId)
                .orElseThrow(UrlNotFoundException::new);

        User currentUser = authenticatedUserService.getCurrentUser();
        if (!url.getUser().getUserId().equals(currentUser.getUserId())) {
            throw new UnauthorizedException();
        }
        return urlClickRepository.countByUrl_UrlId(urlId);
    }

    @Cacheable(value = "clicksByDate", key = "#urlId + '-' + #date")
    public Long clickPerDay(UUID urlId, LocalDate date) {
        Url url = urlRepository.findById(urlId)
                .orElseThrow(UrlNotFoundException::new);

        User currentUser = authenticatedUserService.getCurrentUser();
        if (!url.getUser().getUserId().equals(currentUser.getUserId())) {
            throw new UnauthorizedException();
        }
        Instant startOfDay = date.atStartOfDay(ZoneOffset.UTC).toInstant();
        Instant endOfDay = date.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant();
        return urlClickRepository.countByUrl_UrlIdAndVisitedAtBetween(urlId, startOfDay, endOfDay);
    }

    @Cacheable(value = "topCountries", key = "#p0")
    public List<String> topCountries(UUID urlId) {
        Url url = urlRepository.findById(urlId)
                .orElseThrow(UrlNotFoundException::new);

        User currentUser = authenticatedUserService.getCurrentUser();
        if (!url.getUser().getUserId().equals(currentUser.getUserId())) {
            throw new UnauthorizedException();
        }
        return urlClickRepository.findTopCountriesByUrlId(urlId);
    }

    @Cacheable(value = "topBrowsers", key = "#p0")
    public List<String> topBrowsers(UUID urlId) {
        Url url = urlRepository.findById(urlId)
                .orElseThrow(UrlNotFoundException::new);

        User currentUser = authenticatedUserService.getCurrentUser();
        if (!url.getUser().getUserId().equals(currentUser.getUserId())) {
            throw new UnauthorizedException();
        }
        // Implement a method in UrlClickRepository to get top browsers by URL ID
        return urlClickRepository.findTopBrowsersByUrlId(urlId);
    }

}
