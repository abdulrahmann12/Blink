package com.example.Blink.url.controller;

import com.example.Blink.common.dto.BaseResponse;
import com.example.Blink.common.dto.ChangePasswordRequest;
import com.example.Blink.common.messages.Messages;
import com.example.Blink.common.messages.SwaggerMessages;
import com.example.Blink.url.dto.CreateUrlRequest;
import com.example.Blink.url.dto.UpdateUrlRequest;
import com.example.Blink.url.dto.UrlPasswordRequest;
import com.example.Blink.url.service.UrlService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/urls")
@RequiredArgsConstructor
@Tag(name = SwaggerMessages.TAG_URL, description = SwaggerMessages.TAG_URL_DESC)
public class UrlController {
    private final UrlService urlService;

    @Operation(summary = SwaggerMessages.CREATE_SHORT_URL, description = SwaggerMessages.CREATE_SHORT_URL_DESC)
    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public ResponseEntity<BaseResponse> generateShortUrl(@Valid @RequestBody CreateUrlRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new BaseResponse(Messages.URL_CREATED, urlService.generateShortUrl(request)));
    }

    // Public — no auth required
    @Operation(summary = SwaggerMessages.REDIRECT_URL, description = SwaggerMessages.REDIRECT_URL_DESC)
    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirect(@PathVariable String shortCode) {
        String originalUrl = urlService.getOriginalUrl(shortCode);
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(originalUrl))
                .build();
    }

    // Public — no auth required
    @Operation(summary = SwaggerMessages.UNLOCK_URL, description = SwaggerMessages.UNLOCK_URL_DESC)
    @PostMapping("/{shortCode}/unlock")
    public ResponseEntity<Void> unlock(
            @PathVariable String shortCode,
            @Valid @RequestBody UrlPasswordRequest request) {
        String originalUrl = urlService.unlockUrl(shortCode, request.getPassword());
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(originalUrl))
                .build();
    }

    // Public — no auth required
    @Operation(summary = SwaggerMessages.CHECK_URL, description = SwaggerMessages.CHECK_URL_DESC)
    @GetMapping("/check")
    public ResponseEntity<BaseResponse> checkUrl(@RequestParam String url) {
        boolean valid = urlService.checkUrl(url);
        String message = valid ? Messages.URL_VALID : Messages.URL_INVALID;
        return ResponseEntity.ok(new BaseResponse(message, valid));
    }

    @Operation(summary = SwaggerMessages.GET_URL_STATS, description = SwaggerMessages.GET_URL_STATS_DESC)
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{shortCode}/stats")
    public ResponseEntity<BaseResponse> getUrlStats(@PathVariable String shortCode) {
        return ResponseEntity.ok(new BaseResponse(Messages.URL_STATS, urlService.getUrlStats(shortCode)));
    }

    @Operation(summary = SwaggerMessages.TOGGLE_URL_STATUS, description = SwaggerMessages.TOGGLE_URL_STATUS_DESC)
    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/{urlId}/toggle")
    public ResponseEntity<BaseResponse> toggleStatus(@PathVariable UUID urlId) {
        urlService.toggleStatus(urlId);
        return ResponseEntity.ok(new BaseResponse(Messages.URL_TOGGLED, null));
    }

    @Operation(summary = SwaggerMessages.GET_URL_BY_ID, description = SwaggerMessages.GET_URL_BY_ID_DESC)
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/id/{urlId}")
    public ResponseEntity<BaseResponse> getUrlById(@PathVariable UUID urlId) {
        return ResponseEntity.ok(new BaseResponse(Messages.URL_FETCHED, urlService.getUrlById(urlId)));
    }

    @Operation(summary = SwaggerMessages.GET_USER_URLS, description = SwaggerMessages.GET_USER_URLS_DESC)
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/my")
    public ResponseEntity<BaseResponse> getUserUrls(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(new BaseResponse(Messages.URLS_FETCHED, urlService.getUserUrls(page, size)));
    }

    @Operation(summary = SwaggerMessages.UPDATE_URL, description = SwaggerMessages.UPDATE_URL_DESC)
    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{urlId}")
    public ResponseEntity<BaseResponse> updateUrl(
            @PathVariable UUID urlId,
            @Valid @RequestBody UpdateUrlRequest request) {
        return ResponseEntity.ok(new BaseResponse(Messages.URL_UPDATED, urlService.updateUrl(urlId, request)));
    }

    @Operation(summary = SwaggerMessages.REMOVE_URL_PASSWORD, description = SwaggerMessages.REMOVE_URL_PASSWORD_DESC)
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{urlId}/password")
    public ResponseEntity<BaseResponse> removePassword(@PathVariable UUID urlId) {
        urlService.removePassword(urlId);
        return ResponseEntity.ok(new BaseResponse(Messages.URL_PASSWORD_REMOVED, null));
    }

    @Operation(summary = SwaggerMessages.GET_DASHBOARD, description = SwaggerMessages.GET_DASHBOARD_DESC)
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/dashboard")
    public ResponseEntity<BaseResponse> getDashboard() {
        return ResponseEntity.ok(new BaseResponse(Messages.DASHBOARD_FETCHED, urlService.getDashboard()));
    }

    @Operation(summary = SwaggerMessages.CHANGE_URL_PASSWORD, description = SwaggerMessages.CHANGE_URL_PASSWORD_DESC)
    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{urlId}/password")
    public ResponseEntity<BaseResponse> changePassword(
            @PathVariable UUID urlId,
            @Valid @RequestBody ChangePasswordRequest request) {
        urlService.changePassword(urlId, request);
        return ResponseEntity.ok(new BaseResponse(Messages.URL_PASSWORD_CHANGED, null));
    }
}

