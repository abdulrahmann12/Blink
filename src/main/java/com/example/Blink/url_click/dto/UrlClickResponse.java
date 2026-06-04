package com.example.Blink.url_click.dto;

import com.example.Blink.url_click.entity.DeviceType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UrlClickResponse {
    private Long clickId;
    private String ipAddress;
    private String browser;
    private String operatingSystem;
    private DeviceType deviceType;
    private String country;
    private String referrer;
    private Instant visitedAt;
}
