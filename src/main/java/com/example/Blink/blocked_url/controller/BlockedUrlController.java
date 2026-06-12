package com.example.Blink.blocked_url.controller;

import com.example.Blink.blocked_url.dto.CreateBlockedUrlRequest;
import com.example.Blink.blocked_url.dto.UpdateBlockedUrlRequest;
import com.example.Blink.blocked_url.service.BlockedUrlService;
import com.example.Blink.common.dto.BaseResponse;
import com.example.Blink.common.messages.Messages;
import com.example.Blink.common.messages.SwaggerMessages;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/blocked-urls")
@RequiredArgsConstructor
@Tag(name = SwaggerMessages.TAG_BLOCKED_URL, description = SwaggerMessages.TAG_BLOCKED_URL_DESC)
public class BlockedUrlController {
    private final BlockedUrlService blockedUrlService;

    @Operation(summary = SwaggerMessages.BLOCK_DOMAIN, description = SwaggerMessages.BLOCK_DOMAIN_DESC)
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse> blockDomain(@Valid @RequestBody CreateBlockedUrlRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new BaseResponse(Messages.DOMAIN_BLOCKED, blockedUrlService.blockDomain(request)));
    }

    @Operation(summary = SwaggerMessages.UPDATE_BLOCKED_URL, description = SwaggerMessages.UPDATE_BLOCKED_URL_DESC)
    @PutMapping("/{blockedUrlId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse> updateBlockedUrl(
            @PathVariable Long blockedUrlId,
            @Valid @RequestBody UpdateBlockedUrlRequest request) {
        return ResponseEntity.ok(new BaseResponse(Messages.BLOCKED_URL_UPDATED, blockedUrlService.updateBlockedUrl(blockedUrlId, request)));
    }

    @Operation(summary = SwaggerMessages.GET_BLOCKED_URL_BY_ID, description = SwaggerMessages.GET_BLOCKED_URL_BY_ID_DESC)
    @GetMapping("/{blockedUrlId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse> getBlockedUrlById(@PathVariable Long blockedUrlId) {
        return ResponseEntity.ok(new BaseResponse(Messages.BLOCKED_URL_FETCHED, blockedUrlService.getBlockedUrlById(blockedUrlId)));
    }

    @Operation(summary = SwaggerMessages.GET_ALL_BLOCKED_URLS, description = SwaggerMessages.GET_ALL_BLOCKED_URLS_DESC)
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse> getBlockedUrls(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(new BaseResponse(Messages.BLOCKED_URLS_FETCHED, blockedUrlService.getBlockedUrls(page, size)));
    }

    @Operation(summary = SwaggerMessages.CHECK_DOMAIN_BLOCKED, description = SwaggerMessages.CHECK_DOMAIN_BLOCKED_DESC)
    @GetMapping("/check")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse> isDomainBlocked(@RequestParam String domain) {
        return ResponseEntity.ok(new BaseResponse(Messages.DOMAIN_CHECK_RESULT, blockedUrlService.isDomainBlocked(domain)));
    }

    @Operation(summary = SwaggerMessages.UNBLOCK_DOMAIN, description = SwaggerMessages.UNBLOCK_DOMAIN_DESC)
    @DeleteMapping("/{blockedUrlId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse> unblockDomain(@PathVariable Long blockedUrlId) {
        blockedUrlService.unblockDomain(blockedUrlId);
        return ResponseEntity.ok(new BaseResponse(Messages.DOMAIN_UNBLOCKED));
    }
}
