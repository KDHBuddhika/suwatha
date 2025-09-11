package com.spring.Suwatha.analytics_module.dto.reportDto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;


@Data
public class ReportListViewDto {
    private Long sessionId;
    private String patientHandle;
    private String therapistName;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;
    private String illness;
    private String city;
    private String therapistMessage;
    private String gender;
    private Integer age;
    private String riskAssessment;
}
