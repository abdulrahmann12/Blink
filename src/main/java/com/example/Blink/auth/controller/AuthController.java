package com.example.Blink.auth.controller;

import com.example.Blink.auth.dto.*;
import com.example.Blink.auth.service.AuthService;
import com.example.Blink.common.dto.BaseResponse;
import com.example.Blink.common.dto.ChangePasswordRequest;
import com.example.Blink.common.messages.Messages;
import com.example.Blink.common.messages.SwaggerMessages;
import com.example.Blink.user.dto.CreateUserRequest;
import com.example.Blink.user.dto.VerifyAccountRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = SwaggerMessages.TAG_AUTH, description = SwaggerMessages.TAG_AUTH_DESC)
public class AuthController {

    private final AuthService authService;

    @Operation(summary = SwaggerMessages.CREATE_USER, description = SwaggerMessages.CREATE_USER_DESC)
    @PostMapping("/register")
    public ResponseEntity<BaseResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new BaseResponse(Messages.USER_CREATED, authService.createUser(request)));
    }

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
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BaseResponse> logout(@Valid @RequestBody RefreshTokenRequest request) {
        authService.logout(request);
        return ResponseEntity.ok(new BaseResponse(Messages.LOGOUT_SUCCESS));
    }

    @Operation(summary = SwaggerMessages.VERIFY_ACCOUNT, description = SwaggerMessages.VERIFY_ACCOUNT_DESC)
    @PostMapping("/verify-account")
    public ResponseEntity<BaseResponse> verifyAccount(@Valid @RequestBody VerifyAccountRequest request) {
        authService.verifyAccount(request);
        return ResponseEntity.ok(new BaseResponse(Messages.ACCOUNT_VERIFIED));
    }

    @Operation(summary = SwaggerMessages.REGENERATE_CODE, description = SwaggerMessages.REGENERATE_CODE_DESC)
    @PostMapping("/regenerate-code")
    public ResponseEntity<BaseResponse> regenerateCode(@Valid @RequestBody EmailRequestDTO emailRequestDTO) {
        authService.reGenerateCode(emailRequestDTO);
        return ResponseEntity.ok(new BaseResponse(Messages.VERIFICATION_CODE_REGENERATED));
    }

    @Operation(summary = SwaggerMessages.FORGOT_PASSWORD, description = SwaggerMessages.FORGOT_PASSWORD_DESC)
    @PostMapping("/forget-password")
    public ResponseEntity<BaseResponse> forgetPassword(@Valid @RequestBody EmailRequestDTO emailRequestDTO) {
        authService.forgetPassword(emailRequestDTO);
        return ResponseEntity.ok(new BaseResponse(Messages.RESEND_CODE));
    }

    @Operation(summary = SwaggerMessages.RESET_PASSWORD, description = SwaggerMessages.RESET_PASSWORD_DESC)
    @PostMapping("/reset-password")
    public ResponseEntity<BaseResponse> resetPassword(@RequestBody ResetPasswordRequestDTO request) {
        authService.resetPassword(request);
        return ResponseEntity.ok(new BaseResponse(Messages.RESET_SUCCESS));
    }

    @Operation(summary = SwaggerMessages.CHANGE_PASSWORD, description = SwaggerMessages.CHANGE_PASSWORD_DESC)
    @PostMapping("/change-password")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BaseResponse> changePassword(@RequestBody ChangePasswordRequest request) {
        authService.changePassword(request);
        return ResponseEntity.ok(new BaseResponse(Messages.PASSWORD_CHANGED));
    }
}
