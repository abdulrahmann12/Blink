package com.example.Blink.auth.dto;


import com.example.Blink.common.messages.ValidationMessages;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequestDTO {

    @NotBlank(message = ValidationMessages.USERNAME_OR_EMAIL_REQUIRED)
    private String usernameOrEmail;

    @NotBlank(message = ValidationMessages.PASSWORD_REQUIRED)
    private String password;
}