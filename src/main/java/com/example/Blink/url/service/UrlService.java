package com.example.Blink.url.service;

import com.example.Blink.exception.AliasAlreadyUsed;
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

import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
@Validated
public class UrlService {
    private final UrlRepository urlRepository;
    private final UrlMapper urlMapper;
    private final PasswordEncoder passwordEncoder;

    private static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int SHORT_CODE_LENGTH = 7;
    private static final SecureRandom random = new SecureRandom();

    @Transactional
    public UrlResponse generateShortUrl(@Valid CreateUrlRequest request){

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

        url.setShortUrl(alias != null ? alias : generateUniqueShortCode());
        url.setActive(true);

        Url savedUrl = urlRepository.save(url);
        return urlMapper.toResponse(savedUrl);

    }

    private String generateUniqueShortCode() {
        String code;
        do {
            code = generateShortCode();
        } while (urlRepository.existsByShortUrl(code));
        return code;
    }


    private String generateShortCode() {
        StringBuilder sb = new StringBuilder(SHORT_CODE_LENGTH);
        for (int i = 0; i < SHORT_CODE_LENGTH; i++) {
            sb.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }



}

