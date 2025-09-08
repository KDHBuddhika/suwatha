package com.spring.Suwatha.session_module.dto;

import com.spring.Suwatha.session_module.entity.SessionSummary;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
public class SessionSummaryCreateDto {
    
    private String identifiedIllness;
    
    private String city;
    
    private String therapistPrivateNotes;
    
    private Integer age;
    
    private Gender gender; // Jackson will map the string "FEMALE" to the enum value
    
    @NotNull(message = "A risk assessment must be provided.")
    private RiskAssessment riskAssessment;
    
}
