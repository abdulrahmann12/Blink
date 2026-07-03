package com.example.Blink.auth.dto;

import com.example.Blink.common.messages.ValidationMessages;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResetPasswordRequestDTO {
    @NotBlank(message = ValidationMessages.USERNAME_OR_EMAIL_REQUIRED)
    private String usernameOrEmail;

    @NotBlank(message = ValidationMessages.VERIFICATION_CODE)
    private String code;

    @NotBlank(message = ValidationMessages.NEW_PASSWORD_REQUIRED)
    @Size(min = 8, message = ValidationMessages.NEW_PASSWORD_MIN_SIZE)
    private String newPassword;
}
