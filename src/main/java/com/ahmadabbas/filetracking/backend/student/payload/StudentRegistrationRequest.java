package com.ahmadabbas.filetracking.backend.student.payload;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record StudentRegistrationRequest(
        @NotBlank String name,
        @Email String email,
        @NotBlank String password,
        @NotBlank String program,
        @Positive @Min(1) Short year,
        @NotBlank String picture,
        @NotBlank String advisorId
) {
}
