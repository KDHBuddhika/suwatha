package com.spring.Suwatha.analytics_module.dto.reportDto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ReportFiltersDto {
    private List<String> cities;
    private List<String> illnesses;
    private List<String> riskAssessments;
}
