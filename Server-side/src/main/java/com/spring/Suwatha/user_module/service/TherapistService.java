package com.spring.Suwatha.user_module.service;

import com.spring.Suwatha.file_module.FileService;
import com.spring.Suwatha.shared.email.EmailService;
import com.spring.Suwatha.shared.exception.FileExistsException;
import com.spring.Suwatha.shared.exception.InvalidPasswordException;
import com.spring.Suwatha.shared.exception.ResourceNotFoundException;
import com.spring.Suwatha.user_module.dto.therapist.PasswordChangeDto;
import com.spring.Suwatha.user_module.dto.therapist.TherapistCreateDto;
import com.spring.Suwatha.user_module.dto.therapist.TherapistDetailsUpdateDto;
import com.spring.Suwatha.user_module.dto.therapist.TherapistViewDto;
import com.spring.Suwatha.user_module.entity.Specialization;
import com.spring.Suwatha.user_module.entity.Therapist;
import com.spring.Suwatha.user_module.repository.SpecializationRepository;
import com.spring.Suwatha.user_module.repository.TherapistRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
public class TherapistService {
    
    @Autowired
    private  TherapistRepository therapistRepository;
    
    @Autowired
    private  SpecializationRepository specializationRepository; // Added repository
    
    @Autowired
    private  PasswordEncoder passwordEncoder;
    
    @Autowired
    private  EmailService emailService;
    
    @Autowired
    private  FileService fileService;
    
    @Value("${project.poster.path}")
    private String path;
    
    @Value("${base.url}")
    private String baseUrl;
    
    
    //-----------------------------Create Therapist--------------------------------------------
    
    @Transactional // Good practice for operations that modify multiple tables
    public String createTherapist(TherapistCreateDto dto, MultipartFile file)  throws IOException {
        String temporaryPassword = UUID.randomUUID().toString().substring(0, 8);
    
        System.out.println(temporaryPassword);
    
        if( Files.exists(Paths.get(path + File.separator + file.getOriginalFilename()))){
            throw  new FileExistsException("File Already exist. please enter another file name ");
        }
        String uploadedFileName = fileService.uploadFile(path,file);
        System.out.println(uploadedFileName);
        
        Therapist therapist = new Therapist();
        therapist.setName(dto.getName());
        therapist.setEmail(dto.getEmail());
        therapist.setProfilePictureUrl(uploadedFileName); // Set new field
        therapist.setPasswordHash(passwordEncoder.encode(temporaryPassword));
        therapist.setHandSignSpecialist(dto.isHandSignSpecialist());
        therapist.setActive(false);
        therapist.setCurrentStatus(dto.getCurrentStatus());
        
        // Fetch and set specializations
        if (dto.getSpecializationIds() != null && !dto.getSpecializationIds().isEmpty()) {
            Set<Specialization> specializations = specializationRepository.findByIdIn(dto.getSpecializationIds());
            therapist.setSpecializations(specializations);
        }
        
        Therapist savedTherapist = therapistRepository.save(therapist);
        
        String emailSubject = "Welcome to the Mental Health Platform";
        String emailText = "Hello " + savedTherapist.getName() + ",\n\nYour account has been created."
                + "\nYour temporary password is: " + temporaryPassword
                + "\nPlease change it after your first login.";
        emailService.sendSimpleMail(savedTherapist.getEmail(), emailSubject, emailText);
        
        return "created successfully";
    }
    
    
    
    //---------------------------------- Get All Therapist --------------------------------------------
    public List<TherapistViewDto> getAllTherapists() {
        List<Therapist> therapists = therapistRepository.findAll();
        if(therapists.isEmpty()){
            throw new RuntimeException("Therapists Do not Found");
        }
        return therapists.stream()
                .map(this::toTherapistViewDto)
                .collect(Collectors.toList());
    
    }
    private TherapistViewDto toTherapistViewDto(Therapist therapist) {
        TherapistViewDto dto = new TherapistViewDto();
        String posterUrl =baseUrl + "/file/" +therapist.getProfilePictureUrl();
        dto.setId(therapist.getId());
        dto.setName(therapist.getName());
        dto.setEmail(therapist.getEmail());
        dto.setProfilePictureUrl(posterUrl);
        dto.setActive(therapist.isActive());
        dto.setHandSignSpecialist(therapist.isHandSignSpecialist());
        dto.setCurrentStatus(therapist.getCurrentStatus().name());
        
        dto.setSpecializations(therapist.getSpecializations().stream()
                .map(Specialization::getName)
                .collect(Collectors.toList()));
        
        return dto;
    }
    
    
    
    //---------------------------------- Update Therapist's details by id --------------------------------------------
    public  TherapistViewDto updateTherapistDetailsById(Long id , TherapistDetailsUpdateDto dto){
        Therapist therapist = therapistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Therapist Not Found with Id :" + id));
        Optional.ofNullable(dto.getName()).ifPresent(therapist::setName);
        Optional.ofNullable(dto.getEmail()).ifPresent(therapist::setEmail);
        Optional.ofNullable(dto.getIsActive()).ifPresent(therapist::setActive);
        Optional.ofNullable(dto.getIsHandSignSpecialist()).ifPresent(therapist::setHandSignSpecialist);
        
        if(dto.getSpecializationIds() != null){
            if(dto.getSpecializationIds().isEmpty()){
                therapist.getSpecializations().clear();
            }else{
                Set<Specialization> specializations = specializationRepository.findByIdIn(dto.getSpecializationIds());
                therapist.setSpecializations(specializations);
            }
        }
        Therapist updatedTherapist = therapistRepository.save(therapist);
        
        return toTherapistViewDto(updatedTherapist);
       
    }
    
    
    
    //------------------------ Get Therapist By id --------------------------------------
    
    public TherapistViewDto getTherapistById(Long id){
        Therapist therapist = therapistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Can not found therapist by this id :" + id));
    
        String posterUrl =baseUrl + "/file/" +therapist.getProfilePictureUrl();
        
        TherapistViewDto dto = new TherapistViewDto();
        dto.setEmail(therapist.getEmail());
        dto.setName(therapist.getName());
        dto.setActive(therapist.isActive());
        dto.setCurrentStatus(therapist.getCurrentStatus().name());
        dto.setId(therapist.getId());
        dto.setHandSignSpecialist(therapist.isHandSignSpecialist());
        dto.setProfilePictureUrl(posterUrl);
    
    
        dto.setSpecializations(therapist.getSpecializations().stream()
                .map(Specialization::getName)
                .collect(Collectors.toList()));
        
        return dto;
        
    }
    
    
    
    //------------------------- Change Therapist Password By Id--------------------------
    @Transactional
    public String changeTherapistPassword(Long therapistId, PasswordChangeDto dto) {
  
        Therapist therapist = therapistRepository.findById(therapistId)
                .orElseThrow(() -> new ResourceNotFoundException("Therapist not found with id: " + therapistId));
    
    
        boolean passwordsMatch = passwordEncoder.matches(dto.getCurrentPassword(), therapist.getPasswordHash());
    
        // --- ADD THIS DEBUG BLOCK ---
        System.out.println("\n--- [CHANGE PWD] Checking Therapist ID: " + therapistId + " ---");
        System.out.println("Input 'currentPassword' from Request: " + dto.getCurrentPassword());
        System.out.println("Hash Retrieved from DB for comparison:  " + therapist.getPasswordHash());
        System.out.println("Result of passwordEncoder.matches():    " + passwordsMatch);
        System.out.println("----------------------------------------------------\n");
        // --- END DEBUG BLOCK ---
    
        if (!passwordsMatch) {
            throw new InvalidPasswordException("The provided current password is incorrect.");
        }
        
        
    
            String newPasswordHash = passwordEncoder.encode(dto.getNewPassword());
    
            
            therapist.setPasswordHash(newPasswordHash);
          
    
           
    
    
            String emailSubject = "Your Password Has Been Changed";
            String emailText = "Hello " + therapist.getName() + ",\n\nThis is a notification to inform you that your password on the "
                    + "Mental Health Platform was recently changed by an administrator.\n\n"
                    + "If you did not authorize this change, please contact support immediately.";
            emailService.sendSimpleMail(therapist.getEmail(), emailSubject, emailText);
    
            return "Changed Password Successfully ";
        
    
      
        
    }
    
    
    
    
}
