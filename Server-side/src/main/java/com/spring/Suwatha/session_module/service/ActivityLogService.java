package com.spring.Suwatha.session_module.service;


import com.spring.Suwatha.session_module.entity.ActivityLog;
import com.spring.Suwatha.session_module.repo.ActivityLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class ActivityLogService {
    
    @Autowired
    private ActivityLogRepository activityLogRepository;
    
   
    public void logActivity(String description) {
        ActivityLog log = new ActivityLog(description);
        activityLogRepository.save(log);
    }
    
    
    //-------------------  get all leatest activity logs -----------------------------
    
    public List<ActivityLog> getRecentLogs(int limit) {
        if (limit <= 0) {
            limit = 10;
        }
        return activityLogRepository.findAllByOrderByTimestampDesc(PageRequest.of(0, limit));
    
    }
    
    //-------------------  get activity logs by date -----------------------------
    
    public List<ActivityLog> getLogsForDate(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();           // e.g., 2025-08-15T00:00:00
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);       // e.g., 2025-08-15T23:59:59.999...
        return activityLogRepository.findByDateRange(startOfDay, endOfDay);
    }
    
    
}
