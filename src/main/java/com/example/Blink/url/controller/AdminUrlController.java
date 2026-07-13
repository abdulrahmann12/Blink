package com.example.Blink.url.controller;

import com.example.Blink.common.dto.BaseResponse;
import com.example.Blink.common.messages.Messages;
import com.example.Blink.common.messages.SwaggerMessages;
import com.example.Blink.url.service.AdminUrlService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/urls")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_ADMIN')")
@Tag(name = SwaggerMessages.TAG_ADMIN_URL, description = SwaggerMessages.TAG_ADMIN_URL_DESC)
public class AdminUrlController {

    private final AdminUrlService adminUrlService;

    @Operation(summary = SwaggerMessages.ADMIN_GET_ALL_URLS, description = SwaggerMessages.ADMIN_GET_ALL_URLS_DESC)
    @GetMapping
    public ResponseEntity<BaseResponse> getAllUrls(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(new BaseResponse(Messages.ADMIN_URLS_FETCHED, adminUrlService.getAllUrls(page, size)));
    }

    @Operation(summary = SwaggerMessages.ADMIN_SEARCH_URLS, description = SwaggerMessages.ADMIN_SEARCH_URLS_DESC)
    @GetMapping("/search")
    public ResponseEntity<BaseResponse> searchUrls(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(new BaseResponse(Messages.ADMIN_URLS_FETCHED, adminUrlService.searchUrls(keyword, page, size)));
    }

    @Operation(summary = SwaggerMessages.ADMIN_GET_INACTIVE_URLS, description = SwaggerMessages.ADMIN_GET_INACTIVE_URLS_DESC)
    @GetMapping("/inactive")
    public ResponseEntity<BaseResponse> getInactiveUrls(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(new BaseResponse(Messages.ADMIN_URLS_FETCHED, adminUrlService.getInactiveUrls(page, size)));
    }

    @Operation(summary = SwaggerMessages.ADMIN_GET_EXPIRED_URLS, description = SwaggerMessages.ADMIN_GET_EXPIRED_URLS_DESC)
    @GetMapping("/expired")
    public ResponseEntity<BaseResponse> getExpiredUrls(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(new BaseResponse(Messages.ADMIN_URLS_FETCHED, adminUrlService.getExpiredUrls(page, size)));
    }

    @Operation(summary = SwaggerMessages.ADMIN_GET_TOP_URLS, description = SwaggerMessages.ADMIN_GET_TOP_URLS_DESC)
    @GetMapping("/top")
    public ResponseEntity<BaseResponse> getTopUrls(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(new BaseResponse(Messages.ADMIN_URLS_FETCHED, adminUrlService.getTopUrls(page, size)));
    }

    @Operation(summary = SwaggerMessages.ADMIN_GET_DASHBOARD, description = SwaggerMessages.ADMIN_GET_DASHBOARD_DESC)
    @GetMapping("/dashboard")
    public ResponseEntity<BaseResponse> getDashboard() {
        return ResponseEntity.ok(new BaseResponse(Messages.ADMIN_DASHBOARD_FETCHED, adminUrlService.getDashboard()));
    }

    @Operation(summary = SwaggerMessages.GET_USER_URLS, description = SwaggerMessages.GET_USER_URLS_DESC)
    @GetMapping("/users/{userId}")
    public ResponseEntity<BaseResponse> getUserUrls(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(new BaseResponse(Messages.URLS_FETCHED, adminUrlService.getUserUrls(userId,page, size)));
    }
}
