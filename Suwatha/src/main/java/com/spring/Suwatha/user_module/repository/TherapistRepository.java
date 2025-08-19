package com.spring.Suwatha.user_module.repository;

import com.spring.Suwatha.user_module.entity.Therapist;
import com.spring.Suwatha.user_module.entity.TherapistStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface TherapistRepository extends JpaRepository<Therapist,Long> {
    Optional<Therapist> findByEmail(String email);
    
    // Finds the first available therapist who is active and matches the hand-sign specialist requirement.
    Optional<Therapist> findFirstByIsActiveTrueAndCurrentStatusAndIsHandSignSpecialist(
            TherapistStatus status, boolean isHandSignSpecialist);
    
    // Finds the first available therapist who is active and NOT a hand-sign specialist.
    Optional<Therapist> findFirstByIsActiveTrueAndCurrentStatusAndIsHandSignSpecialistFalse(
            TherapistStatus status);
    
}
