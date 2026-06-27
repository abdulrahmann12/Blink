package com.example.Blink.scheduler;

import com.example.Blink.scheduler.service.UrlCleanupService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.Schedules;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UrlCleanupScheduler {
    private final UrlCleanupService urlCleanupService;

    @Scheduled(cron = "0 0 0 1 * * ") // Runs at midnight on the first day of every month
    public void cleanupExpiredUrls() {
        urlCleanupService.removeExpiredUrls();
    }
}
