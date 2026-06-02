package com.example.Blink.auth.controller;

import com.example.Blink.auth.dto.LoginRequestDTO;
import com.example.Blink.auth.dto.RefreshTokenRequest;
import com.example.Blink.auth.service.AuthService;
import com.example.Blink.common.dto.BaseResponse;
import com.example.Blink.common.messages.Messages;
import com.example.Blink.common.messages.SwaggerMessages;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = SwaggerMessages.TAG_AUTH, description = SwaggerMessages.TAG_AUTH_DESC)
public class AuthController {

    private final AuthService authService;

    @Operation(summary = SwaggerMessages.LOGIN, description = SwaggerMessages.LOGIN_DESC)
    @PostMapping("/login")
    public ResponseEntity<BaseResponse> login(@Valid @RequestBody LoginRequestDTO request) {
        return ResponseEntity.ok(new BaseResponse(Messages.LOGIN_SUCCESS, authService.login(request)));
    }

    @Operation(summary = SwaggerMessages.REFRESH_TOKEN, description = SwaggerMessages.REFRESH_TOKEN_DESC)
    @PostMapping("/refresh-token")
    public ResponseEntity<BaseResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(new BaseResponse(Messages.TOKEN_REFRESHED, authService.refreshToken(request)));
    }

    @Operation(summary = SwaggerMessages.LOGOUT, description = SwaggerMessages.LOGOUT_DESC)
    @PostMapping("/logout")
    public ResponseEntity<BaseResponse> logout(@Valid @RequestBody RefreshTokenRequest request) {
        authService.logout(request);
        return ResponseEntity.ok(new BaseResponse(Messages.LOGOUT_SUCCESS));
    }
}
