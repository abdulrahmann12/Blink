package com.example.Blink.url.service;

import com.example.Blink.common.dto.ChangePasswordRequest;
import com.example.Blink.exception.*;
import com.example.Blink.security.AuthenticatedUserService;
import com.example.Blink.url.dto.CreateUrlRequest;
import com.example.Blink.url.dto.DashboardResponse;
import com.example.Blink.url.dto.UpdateUrlRequest;
import com.example.Blink.url.dto.UrlResponse;
import com.example.Blink.url.entity.Url;
import com.example.Blink.url.mapper.UrlMapper;
import com.example.Blink.url.repository.UrlRepository;
import com.example.Blink.url_click.service.UrlClickService;
import com.example.Blink.user.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.beans.factory.annotation.Value;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Validated
public class UrlService {
    private final UrlRepository urlRepository;
    private final UrlMapper urlMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticatedUserService authenticatedUserService;
    private final UrlClickService urlClickService;

    private static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int SHORT_CODE_LENGTH = 7;
    @Value("${app.base-url}")
    private String baseUrl;
    private static final SecureRandom random = new SecureRandom();

    @Transactional
    @CacheEvict(value = "urls", allEntries = true)
    public UrlResponse generateShortUrl(@Valid CreateUrlRequest request){

        User currentUser = authenticatedUserService.getCurrentUser();

        try {
            URI.create(request.getOriginalUrl());
        } catch (Exception e) {
            throw new InvalidUrlException();
        }

        String alias = (request.getCustomAlias() != null && !request.getCustomAlias().isBlank())
                ? request.getCustomAlias().trim()
                : null;

        if(alias != null && urlRepository.existsByCustomAlias(alias)){
            throw new AliasAlreadyUsed();
        }

        Url url = urlMapper.toEntity(request);
        url.setCustomAlias(alias);

        if(request.getPassword() != null && !request.getPassword().isBlank()){
            url.setPasswordProtected(true);
            url.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        }

        String code = (alias != null) ? alias : generateUniqueShortCode();
        url.setShortUrl(baseUrl + code);
        url.setActive(true);
        url.setUser(currentUser);
        Url savedUrl = urlRepository.save(url);
        return urlMapper.toResponse(savedUrl);

    }

    private String generateUniqueShortCode() {
        String code;
        for (int i = 0; i < 10; i++) {

            code = generateShortCode();

            if (!urlRepository.existsByShortUrl(baseUrl + code)) {
                return code;
            }
        }

        throw new ShortCodeExhaustedException();
    }

    private String generateShortCode() {
        StringBuilder sb = new StringBuilder(SHORT_CODE_LENGTH);
        for (int i = 0; i < SHORT_CODE_LENGTH; i++) {
            sb.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }

    @Transactional
    public String getOriginalUrl(String shortCode, HttpServletRequest request) {
        Url url = urlRepository.findByShortUrl(baseUrl + shortCode)
                .orElseThrow(UrlNotFoundException::new);

        validateUrl(url);

        if (url.isPasswordProtected()) {
            throw new UrlLockedException();
        }
        urlRepository.incrementClickCount(url.getUrlId());
        urlClickService.trackClick(url, request);

        return url.getOriginalUrl();
    }

    @Transactional
    public String unlockUrl(String shortCode, String password, HttpServletRequest request) {
        Url url = urlRepository.findByShortUrl(baseUrl + shortCode)
                .orElseThrow(UrlNotFoundException::new);

        validateUrl(url);

        if (!passwordEncoder.matches(password, url.getPasswordHash())) {
            throw new WrongPasswordException();
        }

        urlRepository.incrementClickCount(url.getUrlId());
        urlClickService.trackClick(url, request);

        return url.getOriginalUrl();
    }

    public boolean checkUrl(String rawUrl) {
        try {
            URL url = URI.create(rawUrl).toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("HEAD");
            connection.setConnectTimeout(3000);
            connection.setReadTimeout(3000);
            connection.setInstanceFollowRedirects(true);
            return connection.getResponseCode() < 400;
        } catch (Exception e) {
            return false;
        }
    }

    private void validateUrl(Url url) {
        if (!url.isActive()) {
            throw new UrlNotActiveException();
        }
        if (url.getExpireAt() != null && url.getExpireAt().isBefore(LocalDateTime.now())) {
            throw new UrlExpiredException();
        }
    }

    @Transactional
    public UrlResponse getUrlStats(String shortCode) {
        User currentUser = authenticatedUserService.getCurrentUser();

        Url url = urlRepository.findByShortUrl(baseUrl + shortCode)
                .orElseThrow(UrlNotFoundException::new);

        if (!url.getUser().getUserId().equals(currentUser.getUserId())) {
            throw new UnauthorizedException();
        }

        return urlMapper.toResponse(url);
    }

    @Transactional
    @CacheEvict(value = "urls", allEntries = true)
    public void toggleStatus(UUID urlId) {

        User currentUser = authenticatedUserService.getCurrentUser();

        Url url = urlRepository.findById(urlId)
                .orElseThrow(UrlNotFoundException::new);

        if (!url.getUser().getUserId().equals(currentUser.getUserId())) {
            throw new UnauthorizedException();
        }

        url.setActive(!url.isActive());
    }

    public UrlResponse getUrlById(UUID urlId) {

        User currentUser = authenticatedUserService.getCurrentUser();

        Url url = urlRepository.findByIdWithUser(urlId)
                .orElseThrow(UrlNotFoundException::new);

        boolean isOwner =
                url.getUser().getUserId().equals(currentUser.getUserId());

        boolean isAdmin =
                currentUser.getRole().getRoleName().equals("ADMIN");

        if (!isOwner && !isAdmin) {
            throw new UnauthorizedException();
        }

        return urlMapper.toResponse(url);
    }

    public Page<UrlResponse> getUserUrls(int page, int size) {
        User currentUser = authenticatedUserService.getCurrentUser();
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        return urlRepository.findAllByUser_UserId(currentUser.getUserId(), pageable)
                .map(urlMapper::toResponse);
    }

    @Transactional
    @CacheEvict(value = "urls", allEntries = true)
    public UrlResponse updateUrl(UUID urlId, UpdateUrlRequest request) {

        User currentUser = authenticatedUserService.getCurrentUser();

        Url url = urlRepository.findById(urlId)
                .orElseThrow(UrlNotFoundException::new);

        if (!url.getUser().getUserId().equals(currentUser.getUserId())) {
            throw new UnauthorizedException();
        }

        if (request.getTitle() != null)
            url.setTitle(request.getTitle());

        if (request.getExpireAt() != null)
            url.setExpireAt(request.getExpireAt());

        if (request.getActive() != null)
            url.setActive(request.getActive());

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            url.setPasswordProtected(true);
            url.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        }
        Url updatedUrl = urlRepository.save(url);

        return urlMapper.toResponse(updatedUrl);
    }

    @Transactional
    @CacheEvict(value = "urls", allEntries = true)
    public void removePassword(UUID urlId) {

        User currentUser = authenticatedUserService.getCurrentUser();

        Url url = urlRepository.findById(urlId)
                .orElseThrow(UrlNotFoundException::new);

        if (!url.getUser().getUserId().equals(currentUser.getUserId())) {
            throw new UnauthorizedException();
        }

        url.setPasswordProtected(false);
        url.setPasswordHash(null);
    }


    public DashboardResponse getDashboard() {

        User currentUser = authenticatedUserService.getCurrentUser();

        long total = urlRepository.countByUser(currentUser);
        long active = urlRepository.countByUserAndActiveTrue(currentUser);
        long clicks = urlRepository.sumClicksByUser(currentUser);
        long expired = urlRepository.countExpiredUrls(currentUser, LocalDateTime.now());

        DashboardResponse response = new DashboardResponse();
        response.setTotalUrls(total);
        response.setActiveUrls(active);
        response.setTotalClicks(clicks);
        response.setExpiredUrls(expired);

        return response;
    }

    @Transactional
    @CacheEvict(value = "urls", allEntries = true)
    public void changePassword(UUID urlId, @NonNull ChangePasswordRequest request) {

        User currentUser = authenticatedUserService.getCurrentUser();

        if (request.getNewPassword() == null ||
                request.getNewPassword().isBlank()) {
            throw new InvalidNewPasswordException();
        }

        Url url = urlRepository.findById(urlId)
                .orElseThrow(UrlNotFoundException::new);

        if (!url.getUser().getUserId().equals(currentUser.getUserId())) {
            throw new UnauthorizedException();
        }

        if (url.isPasswordProtected() && url.getPasswordHash() != null) {

            if (!passwordEncoder.matches(
                    request.getCurrentPassword(),
                    url.getPasswordHash())) {

                throw new WrongPasswordException();
            }
        }

        url.setPasswordProtected(true);
        url.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
    }
}

