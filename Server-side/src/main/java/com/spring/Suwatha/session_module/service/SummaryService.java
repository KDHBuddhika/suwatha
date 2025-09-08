package com.spring.Suwatha.session_module.service;


import com.spring.Suwatha.session_module.dto.SessionStatus;
import com.spring.Suwatha.session_module.dto.SessionSummaryCreateDto;
import com.spring.Suwatha.session_module.entity.Session;
import com.spring.Suwatha.session_module.entity.SessionSummary;
import com.spring.Suwatha.session_module.repo.SessionRepository;
import com.spring.Suwatha.session_module.repo.SessionSummaryRepository;
import com.spring.Suwatha.shared.exception.AccessDeniedException;
import com.spring.Suwatha.shared.exception.IllegalStateException;
import com.spring.Suwatha.shared.exception.ResourceNotFoundException;
import com.spring.Suwatha.user_module.entity.Therapist;
import jakarta.transaction.Transactional;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class SummaryService {
    
    private final SessionRepository sessionRepository;
    private final SessionSummaryRepository summaryRepository;
    
    public SummaryService(SessionRepository sessionRepository, SessionSummaryRepository summaryRepository) {
        this.sessionRepository = sessionRepository;
        this.summaryRepository = summaryRepository;
    }
    
    @Transactional
    public void createSessionSummary(Long sessionId, SessionSummaryCreateDto dto, UserDetails currentUser) {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Session with ID " + sessionId + " not found."));
        
        // Ensure session is completed before a summary can be added
        if (session.getStatus() != SessionStatus.COMPLETED) {
            throw new IllegalStateException("A summary can only be added to a completed session.");
        }
        
        // Security Check: Only the assigned therapist can add a summary
        Therapist therapist = session.getTherapist();
        if (!therapist.getEmail().equals(currentUser.getUsername())) {
            throw new AccessDeniedException("You are not authorized to add a summary for this session.");
        }
        
        // Check if summary already exists
        if(summaryRepository.existsById(sessionId)) {
            throw new IllegalStateException("A summary for this session has already been submitted.");
        }
        
        if(session.getStartTime() == null || session.getEndTime() == null){
            throw new IllegalStateException("Cannot calculate duration for session " + sessionId + " because start or end time is missing.");
        }
    
        Duration duration = Duration.between(session.getStartTime(),session.getEndTime());
        
        long durationInMinutes = (long)Math.ceil(duration.getSeconds() / 60.0);
        
        
        // Create and save the summary
        SessionSummary summary = new SessionSummary();
        summary.setSession(session);
        summary.setIdentifiedIllness(dto.getIdentifiedIllness());
        summary.setTherapistPrivateNotes(dto.getTherapistPrivateNotes());
        summary.setCity(dto.getCity());
        summary.setAge(dto.getAge());
        summary.setGender(dto.getGender());
        summary.setDurationInMinutes((int)durationInMinutes);
        summary.setRiskAssessment(dto.getRiskAssessment());
        
        summaryRepository.save(summary);
    }

}
