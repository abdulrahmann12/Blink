package com.example.Blink.url.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminDashboardResponse {
    private long totalUrls;

    private long activeUrls;

    private long inactiveUrls;

    private long expiredUrls;

    private long totalClicks;

    private long totalUsers;

    private long totalActiveUsers;

    private long totalDeActiveUsers;

    private long totalNOtVerifiedUsers;
}
