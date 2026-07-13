package com.example.Blink.user.dto;

public interface UserDashboardProjection {

    long getTotalUsers();

    long getActiveUsers();

    long getInactiveUsers();

    long getNotVerifiedUsers();
}