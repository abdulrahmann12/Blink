package com.example.Blink.url_click.dto;

import com.example.Blink.url_click.entity.DeviceType;
import com.example.Blink.url_click.entity.SourceType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResourceClickResponse {
    private Long clickId;
    private String ipAddress;
    private String browser;
    private String operatingSystem;
    private DeviceType deviceType;
    private SourceType sourceType;
    private String country;
    private String referrer;
    private Instant visitedAt;
}
