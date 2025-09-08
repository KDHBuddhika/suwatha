package com.spring.Suwatha.session_module.entity;


import com.spring.Suwatha.session_module.dto.CommunicationType;
import com.spring.Suwatha.session_module.dto.SessionStatus;
import com.spring.Suwatha.user_module.entity.Therapist;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "sessions")
@Data
@NoArgsConstructor
public class Session {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "therapist_id", nullable = false)
    private Therapist therapist;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CommunicationType communicationType;
    
    @OneToOne(mappedBy = "session", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private SessionFeedback sessionFeedback;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SessionStatus status = SessionStatus.PENDING;
    
    private LocalDateTime startTime;
    
    private LocalDateTime endTime;

    
}
