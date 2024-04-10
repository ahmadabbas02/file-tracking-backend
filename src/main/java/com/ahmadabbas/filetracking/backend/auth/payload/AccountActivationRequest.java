package com.ahmadabbas.filetracking.backend.auth.payload;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AccountActivationRequest(
        @Email @NotBlank String email,
        @NotBlank String otp,
        @NotBlank String password
) {
    public AccountActivationRequest {
        if (otp == null) {
            otp = "";
        }
    }
}
