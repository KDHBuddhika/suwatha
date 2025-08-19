package com.spring.Suwatha.user_module.dto.therapist;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.spring.Suwatha.user_module.entity.TherapistStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class TherapistCreateDto {
    @NotEmpty
    private String name;
    
    @Email
    private String email;
    
    @JsonProperty("isHandSignSpecialist")
    private boolean handSignSpecialist;
    
    @JsonProperty("isActive")
    private boolean active;
    
    
    @Enumerated(EnumType.STRING)
    private TherapistStatus currentStatus = TherapistStatus.OFFLINE;
    
    // We will send a list of IDs for the specializations
    private List<Long> specializationIds;
    

    
}
