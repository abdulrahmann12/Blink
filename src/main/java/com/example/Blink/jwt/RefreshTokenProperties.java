package com.example.Blink.jwt;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

import java.time.Duration;

@Component
@ConfigurationProperties(prefix = "jwt.refresh-token")
@Getter
@Setter
public class RefreshTokenProperties {

    private Duration expirationMinutes;
}