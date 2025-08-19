package com.spring.Suwatha.session_module.entity;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "activity_logs")
@Data
@NoArgsConstructor
public class ActivityLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 512)
    private String description;
    
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime timestamp;
    
    
    public ActivityLog(String description) {
        this.description = description;
    }
    
    
}
