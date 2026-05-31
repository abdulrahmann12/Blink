package com.example.Blink.user.dto;

import com.example.Blink.common.messages.ValidationMessages;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {

    @NotBlank(message = ValidationMessages.USERNAME_NOT_BLANK)
    @Size(min = 6, max = 50, message = ValidationMessages.USERNAME_SIZE)
    private String username;

    @NotBlank(message = ValidationMessages.EMAIL_NOT_BLANK)
    @Email(message = ValidationMessages.EMAIL_INVALID)
    @Size(max = 100, message = ValidationMessages.EMAIL_TOO_LONG)
    private String email;

    @NotBlank(message = ValidationMessages.FULL_NAME_NOT_BLANK)
    private String fullName;
}

