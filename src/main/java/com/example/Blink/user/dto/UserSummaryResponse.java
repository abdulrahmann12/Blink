package com.example.Blink.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSummaryResponse {

    private Long userId;
    private String username;
    private String email;
    private String roleName;
    private boolean active;
    private boolean verify;
}

