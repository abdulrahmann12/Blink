package com.example.Blink.common.dto;

import com.example.Blink.common.messages.ValidationMessages;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordRequest {

    private String currentPassword;

    @NotBlank(message = ValidationMessages.NEW_PASSWORD_REQUIRED)
    @Size(min = 8, message = ValidationMessages.NEW_PASSWORD_MIN_SIZE)
    private String newPassword;
}

