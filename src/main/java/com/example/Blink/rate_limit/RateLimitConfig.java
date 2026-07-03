package com.example.Blink.rate_limit;

import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Map;

@Component
public class RateLimitConfig {
    private final Map<String, RateLimitRule> rules = Map.of(

            "LOGIN",
            new RateLimitRule(
                    5L,
                    Duration.ofMinutes(1)
            ),

            "REGISTER",
            new RateLimitRule(
                    3L,
                    Duration.ofMinutes(1)
            ),

            "CREATE_URL",
            new RateLimitRule(
                    20L,
                    Duration.ofMinutes(1)
            ),

            "QR_GENERATE",
            new RateLimitRule(
                    20L,
                    Duration.ofMinutes(1)
            ),

            "DASHBOARD",
            new RateLimitRule(
                    60L,
                    Duration.ofMinutes(1)
            ),

            "REGENERATE",
            new RateLimitRule(
                    2L,
                    Duration.ofMinutes(1)
            ),

            "RESET-PASSWORD",
            new RateLimitRule(
                    2L,
                    Duration.ofMinutes(1)
            ),

            "STATS",
            new RateLimitRule(
                    60L,
                    Duration.ofMinutes(1)
            )
    );
    public RateLimitRule getRule(String key){
        return rules.get(key);
    }
}
