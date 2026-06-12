package com.example.Blink.blocked_url.dto;

import com.example.Blink.common.messages.ValidationMessages;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateBlockedUrlRequest {

    @NotBlank(message = ValidationMessages.DOMAIN_NOT_BLANK)
    private String domain;

    @NotBlank(message = ValidationMessages.REASON_NOT_BLANK)
    private String reason;
}
