package com.spring.Suwatha.session_module.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;


@Entity
@Table(name = "session_summary")
@Data
@NoArgsConstructor
public class SessionSummary {
    
    @Id
    @Column(name = "session_id")
    private Long id;
    
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
