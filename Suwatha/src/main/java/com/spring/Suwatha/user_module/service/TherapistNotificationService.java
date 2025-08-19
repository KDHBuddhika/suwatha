package com.spring.Suwatha.user_module.service;


import com.spring.Suwatha.shared.exception.ResourceNotFoundException;
import com.spring.Suwatha.user_module.dto.therapist.NotificationMapper;
import com.spring.Suwatha.user_module.dto.therapist.TherapistNotificationViewDto;
import com.spring.Suwatha.user_module.entity.Therapist;
import com.spring.Suwatha.user_module.entity.TherapistNotification;
import com.spring.Suwatha.user_module.repository.TherapistNotificationRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class TherapistNotificationService {
    private TherapistNotificationRepository notificationRepository;
    
    @Transactional
    public void createNotification(Therapist therapist, String message, String linkUrl) {
        TherapistNotification notification = new TherapistNotification();
        notification.setTherapist(therapist);
        notification.setMessage(message);
        notification.setLinkUrl(linkUrl);
        notificationRepository.save(notification);
    }
    
    public List<TherapistNotificationViewDto> getNotificationsForTherapist(Long therapistId) {
        List<TherapistNotification> notifications = notificationRepository.findByTherapistIdOrderByCreatedAtDesc(therapistId);
        // Use the static mapper class to perform the conversion
        return NotificationMapper.toNotificationViewDtoList(notifications);
    }
    
    
    @Transactional
    public TherapistNotificationViewDto markAsRead(Long notificationId, Long therapistId) {
        TherapistNotification notification = notificationRepository.findByIdAndTherapistId(notificationId, therapistId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Notification not found with id " + notificationId + " for this therapist."));
    
        notification.setRead(true);
        TherapistNotification savedNotification = notificationRepository.save(notification);
    
        // Use the static mapper class to perform the conversion
        return NotificationMapper.toNotificationViewDto(savedNotification);
    }
    
    
    
}
