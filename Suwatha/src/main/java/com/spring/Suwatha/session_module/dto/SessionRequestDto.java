package com.spring.Suwatha.session_module.dto;

import com.spring.Suwatha.session_module.entity.Session;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SessionRequestDto {
    @NotNull
    private CommunicationType communicationType;
}
