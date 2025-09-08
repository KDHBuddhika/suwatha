package com.spring.Suwatha.session_module.controller;


import com.spring.Suwatha.session_module.dto.sessionManagement.SessionListViewDto;
import com.spring.Suwatha.session_module.service.SessionQueryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/management/sessions")
@PreAuthorize("hasRole('ADMIN')")
public class SessionManagementController {
    private final SessionQueryService sessionQueryService;
    
    public SessionManagementController(SessionQueryService sessionQueryService) {
        this.sessionQueryService = sessionQueryService;
    }
    
    /**
     * Retrieves a paginated list of all sessions with detailed, joined information.
     * Supports pagination and sorting via URL parameters.
     * Example: GET /api/management/sessions?page=0&size=20&sort=startTime,desc
     *
     * @param pageable Injected by Spring, configured by URL parameters. Defaults to page 0, size 10, sorted by start time descending.
     * @return A paginated list of session details.
     */
    @GetMapping
    public ResponseEntity<Page<SessionListViewDto>> getAllSessions(
            @RequestParam(name = "q", required = false) String searchTerm,
            @RequestParam(name = "status", required = false) String status,
            @RequestParam(name = "type", required = false) String communicationType,
            @PageableDefault(size = 10, sort = "startTime", direction = Sort.Direction.DESC) Pageable pageable) {
    
        Page<SessionListViewDto> sessions = sessionQueryService.getAllSessionsForManagementView(
                searchTerm,
                status,
                communicationType,
                pageable
        );
    
        return ResponseEntity.ok(sessions);
    }
}
