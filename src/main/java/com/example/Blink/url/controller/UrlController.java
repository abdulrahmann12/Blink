package com.example.Blink.url.controller;

import com.example.Blink.common.dto.BaseResponse;
import com.example.Blink.common.messages.Messages;
import com.example.Blink.url.dto.CreateUrlRequest;
import com.example.Blink.url.dto.UrlPasswordRequest;
import com.example.Blink.url.service.UrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/urls")
@RequiredArgsConstructor
public class UrlController {
    private final UrlService urlService;

    @PostMapping
    public ResponseEntity<BaseResponse> generateShortUrl(@Valid @RequestBody CreateUrlRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new BaseResponse(Messages.URL_CREATED, urlService.generateShortUrl(request)));
    }

    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirect(@PathVariable String shortCode) {
        String originalUrl = urlService.getOriginalUrl(shortCode);
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(originalUrl))
                .build();
    }

    @PostMapping("/{shortCode}/unlock")
    public ResponseEntity<Void> unlock(
            @PathVariable String shortCode,
            @Valid @RequestBody UrlPasswordRequest request) {
        String originalUrl = urlService.unlockUrl(shortCode, request.getPassword());
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(originalUrl))
                .build();
    }

    @GetMapping("/check")
    public ResponseEntity<BaseResponse> checkUrl(@RequestParam String url) {
        boolean valid = urlService.checkUrl(url);
        String message = valid ? Messages.URL_VALID : Messages.URL_INVALID;
        return ResponseEntity.ok(new BaseResponse(message, valid));
    }

    @GetMapping("/{shortCode}/stats")
    public ResponseEntity<BaseResponse> getUrlStats(@PathVariable String shortCode) {
        return ResponseEntity.ok(new BaseResponse(Messages.URL_STATS, urlService.getUrlStats(shortCode)));
    }
}
