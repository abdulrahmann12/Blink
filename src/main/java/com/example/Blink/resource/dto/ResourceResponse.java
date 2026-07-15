package com.example.Blink.resource.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResourceResponse {

    private UUID resourceId;

    private String destinationUrl;

    private String title;

    private String userName;

    private boolean passwordProtected;

    private boolean active;

    private long clickCount;

    private Instant expireAt;

    private Instant createdAt;

    private Instant updatedAt;
}