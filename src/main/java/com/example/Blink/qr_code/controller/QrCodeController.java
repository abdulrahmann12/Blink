package com.example.Blink.qr_code.controller;

import com.example.Blink.common.dto.BaseResponse;
import com.example.Blink.common.messages.SwaggerMessages;
import com.example.Blink.qr_code.service.QrCodeService;
import com.example.Blink.resource.dto.CreateResourceRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/qr-codes")
@RequiredArgsConstructor
@Tag(name = SwaggerMessages.TAG_QR_CODE, description = SwaggerMessages.TAG_QR_CODE_DESC)
public class QrCodeController {
    private final QrCodeService qrCodeService;

    @Operation(summary = SwaggerMessages.GENERATE_QR_CODE, description = SwaggerMessages.GENERATE_QR_CODE_DESC)
    @PostMapping("/generate/{urlId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BaseResponse> generateQrCode(@PathVariable UUID urlId) {
        return ResponseEntity.ok(new BaseResponse(SwaggerMessages.QR_CODE_GENERATED, qrCodeService.generateQrCode(urlId)));
    }

    @Operation(summary = SwaggerMessages.CREATE_QR_CODE, description = SwaggerMessages.CREATE_QR_CODE_DESC)    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BaseResponse> createDynamicQrCode(@Valid @RequestBody CreateResourceRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new BaseResponse(SwaggerMessages.QR_CODE_GENERATED, qrCodeService.generateIndependentQrCode(request)));
    }

    @Operation(summary = SwaggerMessages.GET_QR_CODE, description = SwaggerMessages.GET_QR_CODE_DESC)
    @GetMapping("/{urlId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BaseResponse> getQrCode(@PathVariable UUID urlId) {
        return ResponseEntity.ok(new BaseResponse(SwaggerMessages.QR_CODE_RETRIEVED, qrCodeService.getQrCode(urlId)));
    }

    @Operation(summary = SwaggerMessages.DELETE_QR_CODE, description = SwaggerMessages.DELETE_QR_CODE_DESC)
    @DeleteMapping("/{qrId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BaseResponse> deleteQrCode(@PathVariable UUID qrId) {
        qrCodeService.deleteQrCode(qrId);
        return ResponseEntity.ok(new BaseResponse(SwaggerMessages.QR_CODE_DELETED));
    }
}
