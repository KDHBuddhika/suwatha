package com.spring.Suwatha.session_module.dto;

import lombok.Data;


@Data
public class SessionSummaryCreateDto {
    
    private String identifiedIllness;
    
    private String city;
    
    private String therapistPrivateNotes;
}
