package com.dhiram.ecom_pro.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RESETPassword {

    @NotBlank(message = "Password reset token is required")
    private String token;

    @NotBlank(message = "New password cannot be empty")
    private String password;

    @NotBlank(message = "Please confirm your new password")
    private String confirmPassword;
}