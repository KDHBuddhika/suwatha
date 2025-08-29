package com.spring.Suwatha.user_module.dto.therapist;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TherapistNotificationViewDto {
    private Long id;
    private String message;
    private String linkUrl;
    private boolean isRead;
    private LocalDateTime createdAt;
}
