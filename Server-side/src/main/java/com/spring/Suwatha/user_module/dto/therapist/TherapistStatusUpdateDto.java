package com.spring.Suwatha.user_module.dto.therapist;

import com.spring.Suwatha.user_module.entity.TherapistStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
public class TherapistStatusUpdateDto {
    @NotNull(message = "A new status must be provided.")
    private TherapistStatus newStatus;
}
