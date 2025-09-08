package com.spring.Suwatha.analytics_module.dto.reportDto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReportDetailViewDto {
    private Long sessionId;
    private String patientHandle;
    private String therapistName;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime date;
    private Integer durationInMinutes;
    private String communicationType;
    private String city;
    private String illness;
    private String gender;
    private Integer age;
    private String riskAssessment;
    private String therapistMessage; // generatedSummary
    private String clinicalNotes; // therapistPrivateNotes
}
