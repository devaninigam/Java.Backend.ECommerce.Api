package com.dhiram.ecom_pro.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ResetPasswordRequest {
    @NotBlank(message = "Email address is required")
    @Email(message = "Please provide a valid email address")
    private String email;

    @NotBlank(message = "OTP code is required")
    private String otp;

    @NotBlank(message = "New password is required")
    private String password;

    @NotBlank(message = "Please confirm your new password")
    private String confirmPassword;
}