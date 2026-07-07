package com.example.Blink.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private Long userId;
    private String username;
    private String email;
    private String fullName;
    private String profilePictureUrl;
    private String roleName;
    private String verificationCode;
    private boolean active;
    private Instant createdAt;
    private Instant updatedAt;
}

