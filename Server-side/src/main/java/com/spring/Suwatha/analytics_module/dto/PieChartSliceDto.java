package com.spring.Suwatha.analytics_module.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PieChartSliceDto {
    
    private String label;
    private long value;
    private String color;
}
