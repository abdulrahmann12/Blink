package com.example.Blink.blocked_url.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BlockedUrlResponse {
    private Long blockedUrlId;
    private String domain;
    private String reason;
    private Instant blockedAt;
}
