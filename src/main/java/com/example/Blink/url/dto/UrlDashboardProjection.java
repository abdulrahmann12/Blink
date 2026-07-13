package com.example.Blink.url.dto;

public interface  UrlDashboardProjection {

    long getTotalUrls();

    long getActiveUrls();

    long getInactiveUrls();

    long getExpiredUrls();

    long getTotalClicks();
}
