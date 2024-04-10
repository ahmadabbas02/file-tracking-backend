package com.ahmadabbas.filetracking.backend.auth.payload;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record SendActivationEmailRequest(
        @Email @NotBlank String email
) {
}
