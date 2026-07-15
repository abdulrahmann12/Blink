package com.example.Blink.resource.controller;

import com.example.Blink.common.messages.SwaggerMessages;
import com.example.Blink.resource.service.ResourceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/scan")
@RequiredArgsConstructor
@Tag(name = SwaggerMessages.TAG_SCAN, description = SwaggerMessages.TAG_SCAN_DESC)
public class ScanController {

    private final ResourceService resourceService;

    @Operation(summary = SwaggerMessages.SCAN_QR_CODE, description = SwaggerMessages.SCAN_QR_CODE_DESC)
    @GetMapping("/{resourceId}")
    public ResponseEntity<Void> scanQrCode(@PathVariable UUID resourceId, HttpServletRequest request) {

        String destinationUrl = resourceService.resolveScan(resourceId, request);

        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(destinationUrl))
                .build();
    }
}