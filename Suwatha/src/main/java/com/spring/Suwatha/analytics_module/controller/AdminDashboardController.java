package com.spring.Suwatha.analytics_module.controller;


import com.spring.Suwatha.session_module.entity.ActivityLog;
import com.spring.Suwatha.session_module.service.ActivityLogService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/admin/dashboard")
@PreAuthorize("hasRole('ADMIN')")
public class AdminDashboardController {
    
    private final ActivityLogService activityLogService;
    
    public AdminDashboardController(ActivityLogService activityLogService) {
        this.activityLogService = activityLogService;
    }
    

    @GetMapping("/activity-logs/latest")
    public ResponseEntity<List<ActivityLog>> getRecentActivity(
            @RequestParam(value = "limit", defaultValue = "10") int limit) {
        List<ActivityLog> recentActivities = activityLogService.getRecentLogs(limit);
        return ResponseEntity.ok(recentActivities);
    }
    
    /**
     * Endpoint to get all activity logs for a specific day.
     * The date must be in ISO format (YYYY-MM-DD).
     * Usage: GET /api/admin/dashboard/activity-logs/by-date?date=2025-08-15
     *
     * @param date The specific date to query.
     * @return A list of activity logs for that day.
     */
    @GetMapping("/activity-logs/by-date")
    public ResponseEntity<List<ActivityLog>> getActivityByDate(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<ActivityLog> activitiesForDate = activityLogService.getLogsForDate(date);
        return ResponseEntity.ok(activitiesForDate);
    }
    
}
