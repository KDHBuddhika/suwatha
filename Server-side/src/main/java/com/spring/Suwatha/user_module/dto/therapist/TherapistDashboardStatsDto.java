package com.spring.Suwatha.user_module.dto.therapist;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TherapistDashboardStatsDto {
    private long sessionsToday;
    private long sessionsThisWeek;
    private double averageRating;
}
