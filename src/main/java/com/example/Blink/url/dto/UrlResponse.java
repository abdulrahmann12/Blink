package com.example.Blink.url.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UrlResponse {

    private UUID urlId;

    private String originalUrl;

    private String shortUrl;

    private String title;

    private String customAlias;

    private boolean passwordProtected;

    private boolean active;

    private long clickCount;

    private LocalDateTime expireAt;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
