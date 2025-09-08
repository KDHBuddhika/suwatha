package com.spring.Suwatha.session_module.service;


import com.spring.Suwatha.session_module.dto.sessionManagement.SessionListViewDto;
import com.spring.Suwatha.session_module.entity.Session;
import com.spring.Suwatha.session_module.entity.SessionFeedback;
import com.spring.Suwatha.session_module.repo.SessionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SessionQueryService {
    
    private final SessionRepository sessionRepository;
    
    public SessionQueryService(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }
    
    
//    @Transactional(readOnly = true)
//    public Page<SessionListViewDto> getAllSessionsForManagementView(Pageable pageable) {
//        // 1. Fetch the page of fully populated Session entities from the repository.
//        Page<Session> sessionPage = sessionRepository.findAllForManagementView(pageable);
//
//        // 2. Use the .map() function on the Page object to convert each Session entity to a DTO.
//        //    This preserves all pagination information.
//        return sessionPage.map(this::convertToDto);
//    }
    
    @Transactional(readOnly = true)
    public Page<SessionListViewDto> getAllSessionsForManagementView(
            String searchTerm,
            String status,
            String communicationType,
            Pageable pageable) {
        
        // Call the new custom repository method
        Page<Session> sessionPage = sessionRepository.findWithFilters(searchTerm, status, communicationType, pageable);
        
        // The mapping logic remains the same!
        return sessionPage.map(this::convertToDto);
    }
    
    
    private SessionListViewDto convertToDto(Session session) {
        SessionListViewDto dto = new SessionListViewDto();
        
        dto.setSessionId(session.getId());
        dto.setCommunicationType(session.getCommunicationType().name());
        dto.setStartTime(session.getStartTime());
        dto.setEndTime(session.getEndTime());
        dto.setStatus(session.getStatus().name());
        
        // Safely access joined entities because we used JOIN FETCH
        dto.setPatientHandle(session.getPatient().getAnonymousHandle());
        dto.setTherapistName(session.getTherapist().getName());
        
        // **ROBUST NULL HANDLING FOR FEEDBACK**
        SessionFeedback feedback = session.getSessionFeedback();
        if (feedback != null) {
            dto.setRating(feedback.getRating());
        } else {
            dto.setRating(null); // Explicitly set to null if no feedback exists
        }
        
        // Calculate the duration
        dto.calculateAndSetDuration();
        
        return dto;
    }
    
}
