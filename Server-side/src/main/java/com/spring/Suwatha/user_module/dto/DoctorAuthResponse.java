package com.spring.Suwatha.user_module.dto;

import com.spring.Suwatha.user_module.entity.Therapist;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Optional;


@Data
@AllArgsConstructor
public class DoctorAuthResponse {
    private String token;
    private Optional<Therapist> therapist;
}
