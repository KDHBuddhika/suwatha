package com.spring.Suwatha.analytics_module.controller;


import com.spring.Suwatha.analytics_module.dto.BarChartDataDto;
import com.spring.Suwatha.analytics_module.dto.PieChartSliceDto;
import com.spring.Suwatha.analytics_module.dto.StatisticsDto;
import com.spring.Suwatha.analytics_module.service.StatisticsService;
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
    private final StatisticsService statisticsService;
    
    public AdminDashboardController(ActivityLogService activityLogService, StatisticsService statisticsService) {
        this.activityLogService = activityLogService;
        this.statisticsService = statisticsService;
    }
    

    @GetMapping("/activity-logs/latest")
    public ResponseEntity<List<ActivityLog>> getRecentActivity(
            @RequestParam(value = "limit", defaultValue = "10") int limit) {
        List<ActivityLog> recentActivities = activityLogService.getRecentLogs(limit);
        return ResponseEntity.ok(recentActivities);
    }
    

    @GetMapping("/activity-logs/by-date")
    public ResponseEntity<List<ActivityLog>> getActivityByDate(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<ActivityLog> activitiesForDate = activityLogService.getLogsForDate(date);
        return ResponseEntity.ok(activitiesForDate);
    }
    
    
    @GetMapping("/statistics")
    public ResponseEntity<StatisticsDto> getDashboardStatistics() {
        StatisticsDto stats = statisticsService.getDashboardStatistics();
        return ResponseEntity.ok(stats);
    }
    
    @GetMapping("/charts/illness-distribution")
    public ResponseEntity<List<PieChartSliceDto>> getIllnessDistributionChart(){
        List<PieChartSliceDto> chartData = statisticsService.getIllnessDistributionPieChart();
        return ResponseEntity.ok(chartData);
    }
    
    @GetMapping("/charts/peak-usage-hourly")
    public ResponseEntity<List<BarChartDataDto>> getPeakUsageHourlyChart() {
        List<BarChartDataDto> chartData = statisticsService.getPeakUsageHourlyBarChart();
        return ResponseEntity.ok(chartData);
    }
    
    @GetMapping("/charts/daily-session-volume")
    public ResponseEntity<List<BarChartDataDto>> getDailySessionVolumeChart() {
        List<BarChartDataDto> chartData = statisticsService.getDailySessionVolumeBarChart();
        return ResponseEntity.ok(chartData);
    }
    
}
