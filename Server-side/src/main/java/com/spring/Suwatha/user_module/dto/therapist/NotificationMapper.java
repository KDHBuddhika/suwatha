package com.spring.Suwatha.user_module.dto.therapist;

import com.spring.Suwatha.user_module.entity.TherapistNotification;

import java.util.List;
import java.util.stream.Collectors;

public class NotificationMapper {
    
    public static TherapistNotificationViewDto toNotificationViewDto(TherapistNotification notification) {
        if (notification == null) {
            return null;
        }
        
        TherapistNotificationViewDto dto = new TherapistNotificationViewDto();
        dto.setId(notification.getId());
        dto.setMessage(notification.getMessage());
        dto.setLinkUrl(notification.getLinkUrl());
        dto.setRead(notification.isRead());
        dto.setCreatedAt(notification.getCreatedAt());
        return dto;
    }
    
    /**
     * Converts a list of Notification entities to a list of NotificationViewDtos.
     * @param notifications The list of entities to convert.
     * @return The resulting list of DTOs.
     */
    public static List<TherapistNotificationViewDto> toNotificationViewDtoList(List<TherapistNotification> notifications) {
        if (notifications == null) {
            return null;
        }
        return notifications.stream()
                .map(NotificationMapper::toNotificationViewDto)
                .collect(Collectors.toList());
    }
}
