package com.spring.Suwatha.analytics_module.service;


import com.spring.Suwatha.analytics_module.dto.BarChartDataDto;
import com.spring.Suwatha.analytics_module.dto.PieChartSliceDto;
import com.spring.Suwatha.analytics_module.dto.StatisticsDto;
import com.spring.Suwatha.analytics_module.projection.IDailySessionCountProjection;
import com.spring.Suwatha.analytics_module.projection.IHourlyUsageProjection;
import com.spring.Suwatha.analytics_module.projection.IIllnessCountProjection;
import com.spring.Suwatha.analytics_module.projection.IStatisticsProjection;
import com.spring.Suwatha.session_module.repo.SessionRepository;
import com.spring.Suwatha.session_module.repo.SessionSummaryRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Service
public class StatisticsService {
    
    private final SessionRepository sessionRepository;
    private final SessionSummaryRepository summaryRepository;
    
    public StatisticsService(SessionRepository sessionRepository,SessionSummaryRepository summaryRepository) {
        this.sessionRepository = sessionRepository;
        this.summaryRepository = summaryRepository;
    }
    
    @Transactional// This is a read-only operation, which can be a performance optimization
    public StatisticsDto getDashboardStatistics() {
        IStatisticsProjection projection = sessionRepository.getDashboardStatistics();
        StatisticsDto dto = new StatisticsDto();
        System.out.println(projection.getTotalTherapists());
        
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
    
    
    //-------------------------------- get Illness Distribution PieChart -----------------------------------------------------
    
    public List<PieChartSliceDto> getIllnessDistributionPieChart() {
        // A predefined list of aesthetically pleasing colors for the chart.
        final List<String> PIE_CHART_COLORS = Arrays.asList(
                "#2563eb", "#7c3aed", "#dc2626", "#059669",
                "#ea580c", "#d946ef", "#16a34a", "#64748b"
        );
        
        // 1. Fetch the raw counts from the database.
        List<IIllnessCountProjection> illnessCounts = summaryRepository.getIllnessCounts();
        
        // 2. Map the raw data to the final DTO, adding colors.
        List<PieChartSliceDto> pieChartData = new ArrayList<>();
        int colorIndex = 0;
        for (IIllnessCountProjection count : illnessCounts) {
            String color = PIE_CHART_COLORS.get(colorIndex % PIE_CHART_COLORS.size());
            pieChartData.add(new PieChartSliceDto(
                    count.getIllness(),
                    count.getCount(),
                    color
            ));
            colorIndex++;
        }
        
        return pieChartData;
    }
    
    
    // ------------------------------------------------- get Peak Usage Hourly Bar Chart -------------------------------------
    public List<BarChartDataDto> getPeakUsageHourlyBarChart() {
        // 1. Fetch the raw data from the database. This might have gaps (e.g., no entry for 3 AM).
        List<IHourlyUsageProjection> hourlyUsage = sessionRepository.getHourlyUsage();
    
        // 2. Convert the list into a Map for easy lookups (Hour -> Count).
        //    Example: { 14: 25, 15: 30, 19: 18 }
        Map<Integer, Long> usageMap = hourlyUsage.stream()
                .collect(Collectors.toMap(IHourlyUsageProjection::getHourOfDay, IHourlyUsageProjection::getSessionCount));
    
        // 3. Create a complete list of 24 hours (0 to 23).
        //    For each hour, check the map for a value. If it's not in the map, use 0.
        return IntStream.range(0, 24)
                .mapToObj(hour -> {
                    String label = String.format("%02d:00", hour); // Format as "00:00", "01:00", etc.
                    long value = usageMap.getOrDefault(hour, 0L); // Get value from map or default to 0
                    return new BarChartDataDto(label, value);
                })
                .collect(Collectors.toList());
    }
    
    
    
    // ---------------------------------------------- get Daily Session Volume Bar Chart -----------------------------------
    public List<BarChartDataDto> getDailySessionVolumeBarChart() {
        // 1. Define the date range: today and 13 days ago (for a total of 14 days).
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(13);
    
        // 2. Fetch the raw session counts from the database within this date range.
        List<IDailySessionCountProjection> dailyCounts = sessionRepository.getDailySessionCountsSince(startDate);
    
        // 3. Convert the list into a Map for easy lookups (LocalDate -> Count).
        Map<LocalDate, Long> countsMap = dailyCounts.stream()
                .collect(Collectors.toMap(
                        // Convert java.sql.Date from projection to LocalDate
                        projection -> projection.getSessionDate().toLocalDate(),
                        IDailySessionCountProjection::getSessionCount
                ));
    
        // 4. Define the desired date format for the chart labels (e.g., "Aug 15").
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd");
    
        // 5. Generate a stream of all 14 dates in the range.
        //    For each date, look up its count in the map (or use 0) and create the DTO.
        return Stream.iterate(startDate, date -> date.plusDays(1))
                .limit(14)
                .map(date -> {
                    String label = date.format(formatter);
                    long value = countsMap.getOrDefault(date, 0L);
                    return new BarChartDataDto(label, value);
                })
                .collect(Collectors.toList());
    }
    
    
    
    
}
