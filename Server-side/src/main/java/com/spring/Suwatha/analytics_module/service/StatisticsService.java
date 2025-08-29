package com.spring.Suwatha.analytics_module.service;


import com.spring.Suwatha.analytics_module.dto.StatisticsDto;
import com.spring.Suwatha.analytics_module.projection.IStatisticsProjection;
import com.spring.Suwatha.session_module.repo.SessionRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class StatisticsService {
    
    private final SessionRepository sessionRepository;
    
    public StatisticsService(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }
    
    @Transactional// This is a read-only operation, which can be a performance optimization
    public StatisticsDto getDashboardStatistics() {
        IStatisticsProjection projection = sessionRepository.getDashboardStatistics();
        StatisticsDto dto = new StatisticsDto();
        
        // Map from the projection to the DTO, providing default values for nulls
        dto.setActiveSessions(Optional.ofNullable(projection.getActiveSessions()).orElse(0));
        dto.setTotalSessions(Optional.ofNullable(projection.getTotalSessions()).orElse(0L));
        dto.setFinishedSessions(Optional.ofNullable(projection.getFinishedSessions()).orElse(0L));
        dto.setSessionsToday(Optional.ofNullable(projection.getSessionsToday()).orElse(0L));
        dto.setTherapistsAvailable(Optional.ofNullable(projection.getTherapistsAvailable()).orElse(0L));
        dto.setTotalTherapists(Optional.ofNullable(projection.getTotalTherapists()).orElse(0L));
        dto.setBusyTherapists(Optional.ofNullable(projection.getBusyTherapists()).orElse(0L));
        dto.setOfflineTherapists(Optional.ofNullable(projection.getOfflineTherapists()).orElse(0L));
        
        // Average rating can be null if no feedback has been submitted yet. Default to 0.0.
        dto.setAverageSessionRating(Optional.ofNullable(projection.getAverageSessionRating()).orElse(0.0));
        
        return dto;
    }
}
