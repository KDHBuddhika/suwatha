package com.spring.Suwatha.user_module.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;


@Data
public class AdminCreateDto {
    
    @Email
    private String email;
    
    @NotEmpty @Size(min = 8)
    private String password;
}
