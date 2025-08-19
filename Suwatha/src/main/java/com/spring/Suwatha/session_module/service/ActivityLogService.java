package com.spring.Suwatha.session_module.service;


import com.spring.Suwatha.session_module.entity.ActivityLog;
import com.spring.Suwatha.session_module.repo.ActivityLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ActivityLogService {
    
    @Autowired
    private ActivityLogRepository activityLogRepository;
    
   
    public void logActivity(String description) {
        ActivityLog log = new ActivityLog(description);
        activityLogRepository.save(log);
    }
}
