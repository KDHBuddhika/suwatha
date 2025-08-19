package com.spring.Suwatha.user_module.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;


@Entity
@Table(name = "therapist_notifications")
@Data
@NoArgsConstructor
public class TherapistNotification {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "therapist_id", nullable = false)
    private Therapist therapist;
    
    @Column(nullable = false, length = 512)
    private String message;
    
    @Column(length = 2048)
    private String linkUrl;
    
    @Column(nullable = false)
    private boolean isRead = false;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
