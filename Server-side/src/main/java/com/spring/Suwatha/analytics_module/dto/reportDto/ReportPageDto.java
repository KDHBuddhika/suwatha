package com.spring.Suwatha.analytics_module.dto.reportDto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;


@Data
@AllArgsConstructor
public class ReportPageDto {
    private List<ReportListViewDto> reports;
    private PaginationDto pagination;
    private ReportSummaryDto summary;
}
