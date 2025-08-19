package com.spring.Suwatha.user_module.dto.therapist;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;


@Data
public class PasswordChangeDto {
    @NotEmpty(message = "Current password cannot be empty")
    private String currentPassword;
    
    @NotEmpty(message = "New password cannot be empty")
    @Size(min = 4, message = "New password must be at least 8 characters long")
    private String newPassword;
    
}
