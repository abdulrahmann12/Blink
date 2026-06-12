package com.example.Blink.blocked_url.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BlockedUrlResponse {
    private Long blockedUrlId;
    private String domain;
    private String reason;
    private LocalDateTime blockedAt;
}
