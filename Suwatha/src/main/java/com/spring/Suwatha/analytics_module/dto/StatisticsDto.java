package com.spring.Suwatha.analytics_module.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class StatisticsDto {
    
    private int activeSessions;
    private long totalSessions; // Use long for total counts
    private long finishedSessions;
    private long sessionsToday;
    private long therapistsAvailable;
    private long totalTherapists;
    private long busyTherapists;
    private long offlineTherapists;
    private double averageSessionRating;
}
