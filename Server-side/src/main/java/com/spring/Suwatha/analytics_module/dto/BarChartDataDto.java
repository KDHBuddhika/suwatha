package com.spring.Suwatha.analytics_module.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BarChartDataDto {
    private String label; // e.g., "14:00"
    private long value;   // e.g., 25 (number of sessions)
}
