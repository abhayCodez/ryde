package com.ryde.authservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LogoutRequest {
    @NotBlank(message = "Refresh Token is required")
    private String refreshToken;
}