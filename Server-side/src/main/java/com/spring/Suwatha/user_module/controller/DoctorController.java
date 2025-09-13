package com.spring.Suwatha.user_module.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.Suwatha.session_module.dto.sessionManagement.SessionListViewDto;
import com.spring.Suwatha.session_module.service.SessionQueryService;
import com.spring.Suwatha.shared.exception.EmptyFileException;
import com.spring.Suwatha.user_module.dto.therapist.*;
import com.spring.Suwatha.user_module.entity.Therapist;
import com.spring.Suwatha.user_module.service.TherapistNotificationService;
import com.spring.Suwatha.user_module.service.TherapistService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/doctor")
public class DoctorController {
    
    @Autowired
    private TherapistService therapistService;
    
    @Autowired
    private TherapistNotificationService notificationService;
    
    @Autowired
    private SessionQueryService sessionQueryService;
    
    @PostMapping("/create-Doctor")
   @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createDoctor(@RequestPart MultipartFile file,
                                          @RequestPart String therapistDto) throws EmptyFileException, IOException {
       if(file.isEmpty()){
           throw new EmptyFileException("File is empty !Please send another files");
       }
       
       TherapistCreateDto therapistCreateDto = convertToTherapistDto(therapistDto);
       
       return new ResponseEntity<>(therapistService.createTherapist(therapistCreateDto,file) , HttpStatus.OK);
       
   }
   
   
   @GetMapping("/get-all-therapist")
   @PreAuthorize("hasRole('ADMIN')")
   public ResponseEntity<List<TherapistViewDto>> getAllTherapist (){
       return ResponseEntity.ok(therapistService.getAllTherapists());
   }
    
    @PutMapping("/update-therapists/details/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','THERAPIST')")
    public ResponseEntity<TherapistViewDto> updateTherapistDetails(
            @PathVariable Long id,
            @Valid @RequestBody TherapistDetailsUpdateDto updateDto) {
        
        TherapistViewDto updatedTherapist = therapistService.updateTherapistDetailsById(id, updateDto);
        return ResponseEntity.ok(updatedTherapist);
    }
    
    
    @PutMapping("/update-therapists/password/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','THERAPIST')")
    public ResponseEntity<String> changeTherapistPassword(
            @PathVariable Long id,
            @Valid @RequestBody PasswordChangeDto passwordChangeDto) {
      
        return ResponseEntity.ok( therapistService.changeTherapistPassword(id, passwordChangeDto));
    }
    
    
    
    @GetMapping("/get-therapists/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','THERAPIST')")
    public ResponseEntity<TherapistViewDto> getTherapistById(@PathVariable Long id){
       TherapistViewDto therapistViewDto = therapistService.getTherapistById(id);
       
       return ResponseEntity.ok(therapistViewDto);
    }
    
    
    
    @GetMapping("/me/notifications/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','THERAPIST')")
    public ResponseEntity<List<TherapistNotificationViewDto>> getMyNotifications(@PathVariable Long id) {
       
        List<TherapistNotificationViewDto> notifications = notificationService.getNotificationsForTherapist(id);
        return ResponseEntity.ok(notifications);
    }
    
    @PutMapping("/notifications/{nid}/read/{pid}")
    public ResponseEntity<TherapistNotificationViewDto> markNotificationAsRead(@PathVariable(name = "nid") Long nid,@PathVariable(name = "pid") Long pid) {
    
        TherapistNotificationViewDto updatedNotification = notificationService.markAsRead(nid, pid);
        return ResponseEntity.ok(updatedNotification);
    }
    
    
    
    // ------------------------------- update status ------------------------------------------
    @PutMapping("/update-status")
    public ResponseEntity<TherapistViewDto> updateMyStatus(
            @AuthenticationPrincipal UserDetails currentUser,
            @Valid @RequestBody TherapistStatusUpdateDto statusUpdateDto) {
        
        String therapistEmail = currentUser.getUsername();
        TherapistViewDto updatedTherapist = therapistService.updateOwnStatus(therapistEmail, statusUpdateDto);
        return ResponseEntity.ok(updatedTherapist);
    }
    
    // ------------------------------- get My Dashboard Stats ------------------------------------------
    @GetMapping("/dashboard-stats")
    public ResponseEntity<TherapistDashboardStatsDto> getMyDashboardStats(
            @AuthenticationPrincipal UserDetails currentUser) {
        
        String therapistEmail = currentUser.getUsername();
        TherapistDashboardStatsDto stats = therapistService.getDashboardStats(therapistEmail);
        return ResponseEntity.ok(stats);
    }
    
    
    // ------------------------------  Session video or chat fetchers enable --------------------------
    
    @GetMapping("/enable")
    @PreAuthorize("hasRole('THERAPIST')")
    public boolean getFeachersEnable() {
        return true;
    }
    
    
    
    /**
     * Retrieves a paginated and filterable list of sessions for the currently
     * authenticated therapist.
     */
    
    @GetMapping("/sessions")
    public ResponseEntity<Page<SessionListViewDto>> getMySessions(
            @AuthenticationPrincipal UserDetails currentUser,
            @RequestParam(name = "q", required = false) String searchTerm, // For patient handle
            @RequestParam(name = "status", required = false) String status,
            @RequestParam(name = "type", required = false) String communicationType,
            @PageableDefault(size = 10, sort = "startTime", direction = Sort.Direction.DESC) Pageable pageable) {
        
        String therapistEmail = currentUser.getUsername();
        Page<SessionListViewDto> sessions = sessionQueryService.getSessionsForTherapist(
                therapistEmail, searchTerm, status, communicationType, pageable);
        
        return ResponseEntity.ok(sessions);
    }
    
    
    
    
    
    //json file convert to dto
    private TherapistCreateDto convertToTherapistDto(String therapistDtoObj) throws JsonProcessingException {
        TherapistCreateDto therapistCreateDto ;
        ObjectMapper objectMapper = new ObjectMapper();
        therapistCreateDto =objectMapper.readValue(therapistDtoObj,TherapistCreateDto.class);
        return therapistCreateDto;
    }
    
    
    


}
