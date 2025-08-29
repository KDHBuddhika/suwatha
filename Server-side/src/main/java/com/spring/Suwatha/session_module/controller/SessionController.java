package com.spring.Suwatha.session_module.controller;


import com.spring.Suwatha.session_module.dto.*;
import com.spring.Suwatha.session_module.service.SessionService;
import com.spring.Suwatha.session_module.service.SummaryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sessions")
public class SessionController {
    
    @Autowired
    private SessionService sessionService;
    
    @Autowired
    private SummaryService summaryService;
    
    
    
    @PostMapping("/request")
    public ResponseEntity<SessionViewDto> requestSession( @RequestBody SessionRequestDto requestDto) {
        SessionViewDto sessionDetails = sessionService.requestAndMatchSession(requestDto);
        return ResponseEntity.ok(sessionDetails);
    }
    
    @PutMapping("/{sessionId}/cancel")
    @PreAuthorize("hasRole('THERAPIST')") // Ensures only a therapist can call this
    public ResponseEntity<Void> cancelSession(
            @PathVariable Long sessionId,
            @RequestBody SessionCancelDto cancelDto,
            @AuthenticationPrincipal UserDetails currentUser) {
        System.out.println( currentUser);
        sessionService.cancelSession(sessionId, cancelDto.getReason(), currentUser);
        return ResponseEntity.ok().build();
    }
    
    @PutMapping("/{sessionId}/end")
    @PreAuthorize("hasRole('THERAPIST')")
    public ResponseEntity<Void> endSession(
            @PathVariable Long sessionId,
            @AuthenticationPrincipal UserDetails currentUser) {
        sessionService.endSession(sessionId, currentUser);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/{sessionId}/summary")
    @PreAuthorize("hasRole('THERAPIST')")
    public ResponseEntity<Void> addSessionSummary(
            @PathVariable Long sessionId,
            @RequestBody SessionSummaryCreateDto summaryDto,
            @AuthenticationPrincipal UserDetails currentUser) {
        
        summaryService.createSessionSummary(sessionId, summaryDto, currentUser);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
    
    
    @PostMapping("/feedback")
    public ResponseEntity<Void> submitFeedback(@Valid @RequestBody FeedbackCreateDto feedbackDto) {
        sessionService.submitFeedback(feedbackDto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
    
}
