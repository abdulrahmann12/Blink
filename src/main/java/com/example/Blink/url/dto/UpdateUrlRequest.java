package com.example.Blink.url.dto;

import com.example.Blink.common.messages.ValidationMessages;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUrlRequest {

    @NotBlank(message = ValidationMessages.TITLE_NOT_BLANK)
    private String title;

    @Future(message = ValidationMessages.EXPIRE_DATE_FUTURE)
    private Instant expireAt;

    private Boolean active;

    private String password;
}
