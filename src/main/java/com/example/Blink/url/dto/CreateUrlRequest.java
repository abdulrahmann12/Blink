package com.example.Blink.url.dto;

import com.example.Blink.common.messages.ValidationMessages;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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

    @NotBlank(message = ValidationMessages.URL_NOT_BLANK)
    @URL(message = ValidationMessages.URL_FORMAT_INVALID)
    @Size(max = 2048, message = ValidationMessages.URL_TOO_LONG)
    private String originalUrl;

    @NotBlank(message = ValidationMessages.TITLE_NOT_BLANK)
    private String title;

    @Pattern(
            regexp = "^$|^[A-Za-z0-9]{7}$",
            message = ValidationMessages.ALIAS_FORMAT_INVALID
    )
    private String customAlias;

    private String password;

    @Future(message = ValidationMessages.EXPIRE_DATE_FUTURE)
    private LocalDateTime expireAt;

}
