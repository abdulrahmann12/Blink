package com.example.Blink.url.service;

import com.example.Blink.exception.AliasAlreadyUsed;
import com.example.Blink.exception.UrlExpiredException;
import com.example.Blink.exception.UrlLockedException;
import com.example.Blink.exception.UrlNotActiveException;
import com.example.Blink.exception.UrlNotFoundException;
import com.example.Blink.exception.InvalidUrlException;
import com.example.Blink.exception.WrongPasswordException;
import com.example.Blink.url.dto.CreateUrlRequest;
import com.example.Blink.url.dto.UrlResponse;
import com.example.Blink.url.entity.Url;
import com.example.Blink.url.mapper.UrlMapper;
import com.example.Blink.url.repository.UrlRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Validated
public class UrlService {
    private final UrlRepository urlRepository;
    private final UrlMapper urlMapper;
    private final PasswordEncoder passwordEncoder;

    private static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int SHORT_CODE_LENGTH = 7;
    private static final String BASE_URL = "https://blink.ly/";
    private static final SecureRandom random = new SecureRandom();

    @Transactional
    public UrlResponse generateShortUrl(@Valid CreateUrlRequest request){

        if (!isValidUrl(request.getOriginalUrl())) {
            throw new InvalidUrlException();
        }

        String alias = (request.getCustomAlias() != null && !request.getCustomAlias().isBlank())
                ? request.getCustomAlias().trim()
                : null;

        if(alias != null && urlRepository.existsByCustomAlias(alias)){
            throw new AliasAlreadyUsed();
        }

        Url url = urlMapper.toEntity(request);
        url.setCustomAlias(alias); // override mapper — normalize blank to null

        if(request.getPassword() != null && !request.getPassword().isBlank()){
            url.setPasswordProtected(true);
            url.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        }

        String code = (alias != null) ? alias : generateUniqueShortCode();
        url.setShortUrl(BASE_URL + code);
        url.setActive(true);

        Url savedUrl = urlRepository.save(url);
        return urlMapper.toResponse(savedUrl);

    }

    private String generateUniqueShortCode() {
        String code;
        do {
            code = generateShortCode();
        } while (urlRepository.existsByShortUrl(BASE_URL + code));
        return code;
    }


    private String generateShortCode() {
        StringBuilder sb = new StringBuilder(SHORT_CODE_LENGTH);
        for (int i = 0; i < SHORT_CODE_LENGTH; i++) {
            sb.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }

    public String getOriginalUrl(String shortCode) {
        Url url = urlRepository.findByShortUrl(BASE_URL + shortCode)
                .orElseThrow(UrlNotFoundException::new);

        validateUrl(url);

        if (url.isPasswordProtected()) {
            throw new UrlLockedException();
        }

        return url.getOriginalUrl();
    }

    public String unlockUrl(String shortCode, String password) {
        Url url = urlRepository.findByShortUrl(BASE_URL + shortCode)
                .orElseThrow(UrlNotFoundException::new);

        validateUrl(url);

        if (!passwordEncoder.matches(password, url.getPasswordHash())) {
            throw new WrongPasswordException();
        }

        return url.getOriginalUrl();
    }

    public boolean isValidUrl(String rawUrl) {
        try {
            URL url = URI.create(rawUrl).toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("HEAD");
            connection.setConnectTimeout(3000);
            connection.setReadTimeout(3000);
            connection.setInstanceFollowRedirects(true);
            int responseCode = connection.getResponseCode();
            return responseCode < 400;
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

    public UrlResponse getUrlStats(String shortCode) {
        Url url = urlRepository.findByShortUrl(BASE_URL + shortCode)
                .orElseThrow(UrlNotFoundException::new);
        return urlMapper.toResponse(url);
    }
}

