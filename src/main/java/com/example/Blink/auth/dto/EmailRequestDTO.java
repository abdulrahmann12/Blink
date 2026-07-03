package com.example.Blink.auth.dto;

import com.example.Blink.common.messages.ValidationMessages;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailRequestDTO {
    @NotBlank(message = ValidationMessages.USERNAME_OR_EMAIL_REQUIRED)
    private String usernameOrEmail;
}
