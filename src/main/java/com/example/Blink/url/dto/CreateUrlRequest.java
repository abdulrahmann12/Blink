package com.example.Blink.url.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateUrlRequest {

    @NotBlank(message = "Original URL cannot be blank")
    @URL(message = "Must be a valid URL")
    @Size(max = 2048, message = "URL must not exceed 2048 characters")
    private String originalUrl;

    private String title;

    @Size(max = 100, message = "Custom alias must not exceed 100 characters")
    private String customAlias;

    private boolean passwordProtected;

    private String password;

    @Future(message = "Expiry date must be in the future")
    private LocalDateTime expireAt;

}
