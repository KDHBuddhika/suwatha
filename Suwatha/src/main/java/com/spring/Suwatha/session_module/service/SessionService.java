package com.spring.Suwatha.session_module.service;


import com.spring.Suwatha.session_module.dto.*;
import com.spring.Suwatha.session_module.entity.Patient;
import com.spring.Suwatha.session_module.entity.Session;
import com.spring.Suwatha.session_module.entity.SessionFeedback;
import com.spring.Suwatha.session_module.repo.PatientRepository;
import com.spring.Suwatha.session_module.repo.SessionFeedbackRepository;
import com.spring.Suwatha.session_module.repo.SessionRepository;
import com.spring.Suwatha.shared.email.EmailService;
import com.spring.Suwatha.shared.exception.AccessDeniedException;
import com.spring.Suwatha.shared.exception.IllegalStateException;
import com.spring.Suwatha.shared.exception.ResourceNotFoundException;
import com.spring.Suwatha.user_module.entity.Therapist;
import com.spring.Suwatha.user_module.entity.TherapistStatus;
import com.spring.Suwatha.user_module.repository.TherapistRepository;
import com.spring.Suwatha.user_module.service.TherapistNotificationService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
@Service
public class SessionService {
    
    private PatientRepository patientRepository;
    private TherapistRepository therapistRepository;
    private SessionRepository sessionRepository;
    private SessionFeedbackRepository feedbackRepository;
    private EmailService emailService;
    private  ActivityLogService activityLogService;
    private TherapistNotificationService notificationService;
    
    // -----------------------     Start session -----------------------------------------------
    
    @Transactional
    public SessionViewDto requestAndMatchSession(SessionRequestDto requestDto) {
        // 1. Create an anonymous patient
        Patient patient = new Patient();
        patient.setAnonymousHandle("Patient-" + UUID.randomUUID().toString().substring(0, 8));
        Patient savedPatient = patientRepository.save(patient);
        
        // 2. Find an available therapist
        Therapist therapist = findAvailableTherapist(requestDto.getCommunicationType())
                .orElseThrow(() -> new ResourceNotFoundException("We're sorry, all therapists are currently busy. Please try again shortly."));
        
        // 3. "Lock" the therapist to prevent them from being assigned to another session
        therapist.setCurrentStatus(TherapistStatus.BUSY);
        therapistRepository.save(therapist);
        
        // 4. Create the session
        Session session = new Session();
        session.setPatient(savedPatient);
        session.setTherapist(therapist);
        session.setCommunicationType(requestDto.getCommunicationType());
        session.setStatus(SessionStatus.ACTIVE);
        session.setStartTime(LocalDateTime.now());
        Session savedSession = sessionRepository.save(session);
        
        // 5. Log the activity for the admin panel
        String logMessage = String.format("Dr. %s started a %s session with %s.",
                therapist.getName(),
                requestDto.getCommunicationType().name().toLowerCase(),
                savedPatient.getAnonymousHandle()
        );
        activityLogService.logActivity(logMessage);
    
        String sessionUrl;
        if(requestDto.getCommunicationType() == CommunicationType.CHAT){
            sessionUrl = "https://your-app-domain.com/session/chat/" + savedSession.getId();
        }else{
            sessionUrl = "https://your-app-domain.com/session/video/" + savedSession.getId();
        }
        
        // 6. Send notifications
        
        sendNotifications(therapist, savedPatient, sessionUrl,savedSession.getCommunicationType());
        
        // 7. Return the session details to the patient frontend
        return new SessionViewDto(savedSession.getId(), therapist.getId(), therapist.getName(), sessionUrl);
    }
    
    
    
    //------------------------------------------ Cancel session ------------------------------------------
    public void cancelSession(Long sessionId, String reason , UserDetails currentUser){
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Session with ID " + sessionId + " not found."));
    
        if (session.getStatus() == SessionStatus.COMPLETED || session.getStatus() == SessionStatus.CANCELLED) {
            throw new IllegalStateException("Session is already completed or cancelled and cannot be changed.");
        }
        
        Therapist therapist = session.getTherapist();
    
        if (!therapist.getEmail().equals(currentUser.getUsername())) {
            throw new AccessDeniedException("You are not authorized to cancel this session.");
        }
    
        session.setStatus(SessionStatus.CANCELLED);
        session.setEndTime(LocalDateTime.now()); // Mark when it was cancelled
        therapist.setCurrentStatus(TherapistStatus.AVAILABLE);
    
        sessionRepository.save(session);
        therapistRepository.save(therapist);
    
        String logMessage = String.format("Dr. %s cancelled a session with %s. Reason: %s",
                therapist.getName(),
                session.getPatient().getAnonymousHandle(),
                reason
        );
        activityLogService.logActivity(logMessage);
    
    
    }
    
    
    //---------------------------- End session -----------------------------------------
    @Transactional
    public void endSession (Long sessionId, UserDetails currentUser ){
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Session with ID \" + sessionId + \" not found."));
        
        if(session.getStatus() != SessionStatus.ACTIVE){
            throw new IllegalStateException("Only an active session can be ended.");
        }
    
        Therapist therapist = session.getTherapist();
    
        if (!therapist.getEmail().equals(currentUser.getUsername())) {
            throw new AccessDeniedException("You are not authorized to end this session.");
        }
    
        session.setStatus(SessionStatus.COMPLETED);
        session.setEndTime(LocalDateTime.now());
        sessionRepository.save(session);
    
        therapist.setCurrentStatus(TherapistStatus.AVAILABLE);
        therapistRepository.save(therapist);
    
        // 5. Log the activity
        String logMessage = String.format("Dr. %s completed a session with %s.",
                therapist.getName(),
                session.getPatient().getAnonymousHandle()
        );
        activityLogService.logActivity(logMessage);
    }
    
    
    
    //-------------- --------------- Submit feedback by session id--------------------
    
    @Transactional
    public void submitFeedback(FeedbackCreateDto dto) {
        
        Session session = sessionRepository.findById(dto.getSessionId())
                .orElseThrow(() -> new ResourceNotFoundException("Session with ID " + dto.getSessionId() + " not found. Cannot submit feedback."));
        
        if (session.getStatus() != SessionStatus.COMPLETED) {
            throw new IllegalStateException("Feedback can only be submitted for a completed session.");
        }
        
        
        if (feedbackRepository.existsById(session.getId())) {
            throw new IllegalStateException("Feedback has already been submitted for this session.");
        }
        
        // 4. If all checks pass, create and save the new feedback entity.
        SessionFeedback feedback = new SessionFeedback();
        feedback.setSession(session); // Link it to the session
        feedback.setRating(dto.getRating());
        feedback.setComments(dto.getComments());
        
        feedbackRepository.save(feedback);
    }
    
    
    
    
    
    private Optional<Therapist> findAvailableTherapist(CommunicationType type) {
        if (type == CommunicationType.SPECIAL_NEEDS) {
            return therapistRepository.findFirstByIsActiveTrueAndCurrentStatusAndIsHandSignSpecialist(
                    TherapistStatus.AVAILABLE, true);
        } else {
            // For general 'video' or 'chat', find any non-specialist therapist first to keep specialists free.
            Optional<Therapist> therapist = therapistRepository.findFirstByIsActiveTrueAndCurrentStatusAndIsHandSignSpecialistFalse(
                  TherapistStatus.AVAILABLE);
            // If no non-specialist is found, it's okay to assign a specialist.
            return therapist.isPresent() ? therapist : therapistRepository.findFirstByIsActiveTrueAndCurrentStatusAndIsHandSignSpecialist(
                   TherapistStatus.AVAILABLE, true);
        }
    }
    
    
    
    
    
    
    private void sendNotifications(Therapist therapist, Patient patient, String sessionUrl,CommunicationType session) {
    
        String notificationMessage = String.format("New %s session request from %s. Please join now.",
                patient.getAnonymousHandle(),
                session.name().toLowerCase());
        notificationService.createNotification(therapist, notificationMessage, sessionUrl);
        
        // Send Email Notification
        String emailSubject = "New Patient Session Request!";
        String emailText = String.format("Hello Dr. %s,\n\nYou have a new session request from %s.\n\n" +
                        "Please join the session immediately using this link: %s\n\nThank you.",
                therapist.getName(), patient.getAnonymousHandle(), sessionUrl);
        emailService.sendSimpleMail(therapist.getEmail(), emailSubject, emailText);
        
        // TODO: Implement Real-time WebSocket Notification
        // In a real application, you would send a message over a WebSocket channel
        // that the therapist's dashboard is subscribed to.
        // e.g., simpMessagingTemplate.convertAndSendToUser(therapist.getEmail(), "/queue/sessions", newSessionPayload);
        System.out.println("WebSocket notification sent to therapist: " + therapist.getEmail() + " for session URL: " + sessionUrl);
    }
    
    
   
    
    
    
    
    
}
