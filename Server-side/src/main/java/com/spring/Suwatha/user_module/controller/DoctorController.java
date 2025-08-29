package com.spring.Suwatha.user_module.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.Suwatha.shared.exception.EmptyFileException;
import com.spring.Suwatha.user_module.dto.therapist.*;
import com.spring.Suwatha.user_module.entity.Therapist;
import com.spring.Suwatha.user_module.service.TherapistNotificationService;
import com.spring.Suwatha.user_module.service.TherapistService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/doctor")
public class DoctorController {
    
    @Autowired
    private TherapistService therapistService;
    
    @Autowired
    private TherapistNotificationService notificationService;

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
    @PreAuthorize("hasRole('ADMIN')")
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
    
    
    
    
    //json file convert to dto
    private TherapistCreateDto convertToTherapistDto(String therapistDtoObj) throws JsonProcessingException {
        TherapistCreateDto therapistCreateDto ;
        ObjectMapper objectMapper = new ObjectMapper();
        therapistCreateDto =objectMapper.readValue(therapistDtoObj,TherapistCreateDto.class);
        return therapistCreateDto;
    }


}
