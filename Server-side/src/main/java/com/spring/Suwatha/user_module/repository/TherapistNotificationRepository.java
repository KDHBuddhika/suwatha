package com.spring.Suwatha.user_module.repository;

import com.spring.Suwatha.user_module.entity.TherapistNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;

@Repository
public interface TherapistNotificationRepository extends JpaRepository<TherapistNotification,Long> {
    
    // Find all notifications for a specific therapist, ordered by most recent first
    List<TherapistNotification> findByTherapistIdOrderByCreatedAtDesc(Long therapistId);
    
    // Find a specific notification belonging to a specific therapist (for security)
    Optional<TherapistNotification> findByIdAndTherapistId(Long id, Long therapistId);
    
}
