package com.example.Blink.url.dto;

import com.example.Blink.common.messages.ValidationMessages;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class AssignShortUrlRequest {
    @Pattern(
            regexp = "^$|^[A-Za-z0-9]{7,40}$",
            message = ValidationMessages.ALIAS_FORMAT_INVALID
    )
    private String customAlias;
}