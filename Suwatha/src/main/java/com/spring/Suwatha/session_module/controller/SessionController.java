package com.spring.Suwatha.session_module.controller;


import com.spring.Suwatha.session_module.dto.SessionCancelDto;
import com.spring.Suwatha.session_module.dto.SessionRequestDto;
import com.spring.Suwatha.session_module.dto.SessionViewDto;
import com.spring.Suwatha.session_module.service.SessionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
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
    
}
