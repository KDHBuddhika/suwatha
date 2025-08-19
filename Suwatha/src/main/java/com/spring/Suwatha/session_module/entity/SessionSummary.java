package com.spring.Suwatha.session_module.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

public class SessionSummary {
    
    @Id
    @Column(name = "session_id")
    private Long id;
    
    /**
     * A One-to-One relationship linking this summary directly to its session.
     * `mapsId` indicates that the primary key of this entity is mapped from the Session entity.
     */
    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "session_id")
    private Session session;
    
//    @Lob
//    @Column(nullable = false)
//    private String generatedSummary;
    
    private String identifiedIllness;
    
    @Column(nullable = true)
    private String city;
    
    @Lob
    private String therapistPrivateNotes;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
