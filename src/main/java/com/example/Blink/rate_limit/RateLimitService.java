package com.example.Blink.rate_limit;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimitService {

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    public Bucket resolveBucket(String key, RateLimitRule rule) {

        return buckets.computeIfAbsent(
                key,
                k -> Bucket.builder()
                        .addLimit(
                                Bandwidth.builder()
                                        .capacity(rule.maxRequests())
                                        .refillGreedy(
                                                rule.maxRequests(),
                                                rule.duration()
                                        )
                                        .build()
                        )
                        .build()
        );
    }

    public boolean tryConsume(
            String key,
            RateLimitRule rule
    ) {
        Bucket bucket = resolveBucket(key, rule);
        return bucket.tryConsume(1);
    }
}