package com.livingrank.dto.admin;

public record AdminDashboardResponse(
    long totalUsers,
    long activeUsers,
    long suspendedUsers,
    long totalReviews,
    long hiddenReviews,
    long pendingScheduledActions,
    long totalStreets
) {}
