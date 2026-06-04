package com.example.Blink.url.dto;

import com.example.Blink.common.messages.ValidationMessages;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUrlRequest {

    private String title;

    private LocalDateTime expireAt;

    private Boolean active;

    private String password;
}
