package com.spring.Suwatha.user_module.dto.therapist;

import jakarta.validation.constraints.Email;
import lombok.Data;

import java.util.List;


@Data
public class TherapistDetailsUpdateDto {
    private String name;
    
    @Email(message = "Email should be valid")
    private String email;
    
    private Boolean isActive;
    
    private Boolean isHandSignSpecialist;
    
    // We allow updating the list of specializations
    private List<Long> specializationIds;
}
