package com.example.Blink.url.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashboardResponse {

    private long totalUrls;
    private long activeUrls;
    private long expiredUrls;
    private long totalClicks;
}