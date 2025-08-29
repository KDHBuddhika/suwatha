package com.spring.Suwatha.session_module.dto;


import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FeedbackCreateDto {
    
    @NotNull
    private Long sessionId;
    
    @NotNull
    @Min(1)
    @Max(5)
    private Integer rating;
    
    private String comments;
}
