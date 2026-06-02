package com.example.Blink.auth.dto;

import com.example.Blink.common.messages.ValidationMessages;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RefreshTokenRequest {

    @NotBlank(message = ValidationMessages.REFRESH_TOKEN_REQUIRED)
    private String refreshToken;
}

