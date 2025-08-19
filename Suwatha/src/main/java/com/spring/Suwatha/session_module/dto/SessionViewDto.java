package com.spring.Suwatha.session_module.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SessionViewDto {
    private Long sessionId;
    private Long therapistId;
    private String therapistName;
    private String sessionUrl; // The URL for the patient to redirect to
}
