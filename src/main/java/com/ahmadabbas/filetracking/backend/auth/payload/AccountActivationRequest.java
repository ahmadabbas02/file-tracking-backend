package com.ahmadabbas.filetracking.backend.auth.payload;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AccountActivationRequest(
        @Email @NotBlank String email,
        @NotBlank String code,
        @NotBlank String password
) {
    public AccountActivationRequest {
        if (code == null) {
            code = "";
        }
    }
}
