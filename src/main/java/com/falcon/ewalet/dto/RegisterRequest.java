package com.falcon.ewalet.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank(message = "Full name is required")
    private String fullName;

    @NotBlank(message = "Phone number is required")
    @Size(min = 10, max = 13, message = "Phone must be 10-13 digits")
    private String phoneNumber;

    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "National ID is required")
    private String nationalId;

    @NotBlank(message = "PIN is required")
    @Size(min = 4, max = 4, message = "PIN must be 4 digits")
    private String pin;
}