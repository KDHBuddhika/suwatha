package com.spring.Suwatha.session_module.dto.sessionManagement;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.spring.Suwatha.session_module.dto.CommunicationType;
import com.spring.Suwatha.session_module.dto.SessionStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalDateTime;


@Data
@NoArgsConstructor
public class SessionListViewDto {
    
    private Long sessionId;
    private String communicationType;
    
    // --- ANNOTATION ADDED HERE ---
    @JsonFormat(pattern = "MMM dd, hh:mm a", locale = "en_US")
    private LocalDateTime startTime;
    
    // --- AND ANNOTATION ADDED HERE ---
    @JsonFormat(pattern = "MMM dd, hh:mm a", locale = "en_US")
    private LocalDateTime endTime;
    
    private String status;
    private String patientHandle;
    private String therapistName;
    private Integer rating;
    private Long durationInMinutes;
    

    
    public void calculateAndSetDuration() {
        if (this.startTime != null && this.endTime != null) {
            this.durationInMinutes = Duration.between(this.startTime, this.endTime).toMinutes();
        } else {
            this.durationInMinutes = null;
        }
    }
}
