package com.example.Blink.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfig {

    public CacheManager cacheManager(){
        SimpleCacheManager manager = new SimpleCacheManager();
        manager.setCaches(Arrays.asList(
                // ── STATIC data: long TTL, rarely mutated ──
                buildCache("roles",          30, TimeUnit.MINUTES, 100),
                buildCache("blockedUrls",    30, TimeUnit.MINUTES, 10_000),

                // ── MEDIUM volatility: normal TTL ──
                buildCache("users",          10, TimeUnit.MINUTES, 5_000),
                buildCache("urls",           10, TimeUnit.MINUTES, 10_000),
                buildCache("qrCodes",        10, TimeUnit.MINUTES, 5_000),

                // ── SHORT TTL: high-frequency lookups that must stay fresh ──
                buildCache("userDetails",     2, TimeUnit.MINUTES, 1_000),
                buildCache("tokens",          2, TimeUnit.MINUTES, 5_000),

                // ── ANALYTICS: short TTL, high-frequency reads ──
                buildCache("totalClicks",     5, TimeUnit.MINUTES, 10_000),
                buildCache("clicksByDate",    5, TimeUnit.MINUTES, 50_000),
                buildCache("topCountries",    5, TimeUnit.MINUTES, 10_000),
                buildCache("topBrowsers",     5, TimeUnit.MINUTES, 10_000),
                buildCache("urlClicks",       2, TimeUnit.MINUTES, 5_000)
        ));
        return manager;
    }

    private CaffeineCache buildCache(String name, long duration, TimeUnit unit, long maxSize) {
        return new CaffeineCache(name,
                Caffeine.newBuilder()
                        .expireAfterWrite(duration, unit)
                        .maximumSize(maxSize)
                        .recordStats()
                        .build());
    }
}
