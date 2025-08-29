package com.spring.Suwatha.user_module.dto.therapist;

import lombok.Data;

import java.util.List;


@Data
public class TherapistViewDto {
    private Long id;
    private String name;
    private String email;
    private String profilePictureUrl;
    private boolean isActive;
    private boolean isHandSignSpecialist;
    private String currentStatus;
    private List<String> specializations;
}
