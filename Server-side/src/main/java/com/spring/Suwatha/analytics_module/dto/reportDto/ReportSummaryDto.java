package com.spring.Suwatha.analytics_module.dto.reportDto;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReportSummaryDto {
    private long totalReports;
    private long uniquePatients;
    private long activeTherapists;
    private double averageDuration;
}
