package com.example.Blink.rate_limit;

import java.time.Duration;

public record RateLimitRule (
        Long maxRequests,
        Duration duration
){}
