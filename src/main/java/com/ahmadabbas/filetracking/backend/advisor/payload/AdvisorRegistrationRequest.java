package com.ahmadabbas.filetracking.backend.advisor.payload;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AdvisorRegistrationRequest(
        @NotBlank String name,
        @Email String email,
        @NotBlank String password
) {
}
