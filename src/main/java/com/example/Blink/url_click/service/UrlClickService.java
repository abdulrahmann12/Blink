package com.example.Blink.url_click.service;

import com.example.Blink.exception.ResourceNotFoundException;
import com.example.Blink.exception.UnauthorizedException;
import com.example.Blink.exception.UrlNotFoundException;
import com.example.Blink.resource.entity.Resource;
import com.example.Blink.resource.repository.ResourceRepository;
import com.example.Blink.security.AuthenticatedUserService;

import com.example.Blink.url_click.dto.ResourceClickResponse;
import com.example.Blink.url_click.entity.DeviceType;
import com.example.Blink.url_click.entity.ResourceClick;
import com.example.Blink.url_click.entity.SourceType;
import com.example.Blink.url_click.mapper.ResourceClickMapper;

import com.example.Blink.url_click.model.ClickTrackingEvent;
import com.example.Blink.url_click.model.UserAgentData;
import com.example.Blink.url_click.repository.ResourceClickRepository;
import com.example.Blink.user.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
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

    private final ResourceClickRepository resourceClickRepository;
    private final ResourceRepository resourceRepository;
    private final ResourceClickMapper resourceClickMapper;
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
    public void trackClick(Resource resource, SourceType sourceType, HttpServletRequest request) {
        String ip = extractClientIp(request);
        String userAgent = request.getHeader("User-Agent");
        String referrer = sanitizeReferrer(request.getHeader("Referer"));
        UserAgentData ua = parseUserAgent(userAgent);


        try {
            eventPublisher.publishEvent(
                    new ClickTrackingEvent(resource.getResourceId(), ip, ua, referrer,sourceType)
            );
        } catch (Exception e) {
            log.warn("Click tracking skipped for URL {}: {}", resource.getResourceId(), e.getMessage());
        }
    }

    @Async("clickTrackingExecutor")
    @EventListener
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "resourceClicks", key = "#event.resourceId()"),
            @CacheEvict(value = "totalClicks", key = "#event.resourceId()"),
            @CacheEvict(value = "topCountries", key = "#event.resourceId()"),
            @CacheEvict(value = "topBrowsers", key = "#event.resourceId()"),
            @CacheEvict(value = "clicksByDate", key = "#event.resourceId() + '-' + T(java.time.LocalDate).now(T(java.time.ZoneOffset).UTC).atStartOfDay(T(java.time.ZoneOffset).UTC).toInstant().toEpochMilli()")
    })
    public void handleClickTrackingEvent(ClickTrackingEvent event) {
        try {
            Resource resource = resourceRepository.getReferenceById(event.resourceId());

            ResourceClick click = ResourceClick.builder()
                    .resource(resource)
                    .ipAddress(event.ip())
                    .browser(event.userAgentData().browser())
                    .operatingSystem(event.userAgentData().os())
                    .deviceType(event.userAgentData().deviceType())
                    .country(geoIpService.getCountry(event.ip()))
                    .sourceType(event.sourceType())
                    .referrer(event.referrer())
                    .visitedAt(Instant.now())
                    .build();

            resourceClickRepository.save(click);

            // الزيادة الآمنة (Atomic Update) لحل الـ Race Condition
            resourceRepository.incrementClickCount(event.resourceId());

        } catch (Exception e) {
            log.error("Failed to track click for URL {}: {}", event.resourceId(), e.getMessage());
        }
    }

    // ========================= Query =========================

    public Page<ResourceClickResponse> getClicksByUrlId(UUID resourceId, int page, int size) {
        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(UrlNotFoundException::new);

        User currentUser = authenticatedUserService.getCurrentUser();
        if (!resource.getUser().getUserId().equals(currentUser.getUserId())) {
            throw new UnauthorizedException();
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<ResourceClick> clicks = resourceClickRepository.findByResource_ResourceId(resourceId, pageable);
        return clicks.map(resourceClickMapper::toResourceClickResponse);
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
        DeviceType deviceType;

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

// ========================= Statistics Queries =========================

    public Long totalClick(UUID resourceId) {
        Resource resource = resourceRepository.findByIdWithUser(resourceId)
                .orElseThrow(ResourceNotFoundException::new);

        User currentUser = authenticatedUserService.getCurrentUser();
        if (!resource.getUser().getUserId().equals(currentUser.getUserId())) {
            throw new UnauthorizedException();
        }
        return resourceClickRepository.countByResource_ResourceId(resourceId);
    }

    public Long clickPerDay(UUID resourceId, LocalDate date) {
        Resource resource = resourceRepository.findByIdWithUser(resourceId)
                .orElseThrow(ResourceNotFoundException::new);

        User currentUser = authenticatedUserService.getCurrentUser();
        if (!resource.getUser().getUserId().equals(currentUser.getUserId())) {
            throw new UnauthorizedException();
        }
        Instant startOfDay = date.atStartOfDay(ZoneOffset.UTC).toInstant();
        Instant endOfDay = date.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant();
        return resourceClickRepository.countByResource_ResourceIdAndVisitedAtBetween(resourceId, startOfDay, endOfDay);
    }

    public List<String> topCountries(UUID resourceId) {
        Resource resource = resourceRepository.findByIdWithUser(resourceId)
                .orElseThrow(ResourceNotFoundException::new);

        User currentUser = authenticatedUserService.getCurrentUser();
        if (!resource.getUser().getUserId().equals(currentUser.getUserId())) {
            throw new UnauthorizedException();
        }
        return resourceClickRepository.findTopCountriesByResource_ResourceId(resourceId);
    }

    public List<String> topBrowsers(UUID resourceId) {
        Resource resource = resourceRepository.findByIdWithUser(resourceId)
                .orElseThrow(ResourceNotFoundException::new);

        User currentUser = authenticatedUserService.getCurrentUser();
        if (!resource.getUser().getUserId().equals(currentUser.getUserId())) {
            throw new UnauthorizedException();
        }
        return resourceClickRepository.findTopBrowsersByResource_ResourceId(resourceId);
    }
}
