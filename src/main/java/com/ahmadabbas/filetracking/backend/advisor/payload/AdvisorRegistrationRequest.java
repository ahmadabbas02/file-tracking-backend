package com.ahmadabbas.filetracking.backend.advisor.payload;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;

public record AdvisorRegistrationRequest(
        @NotEmpty(message = "Advisor name should not be empty") String name,
        @NotEmpty(message = "Advisor surname should not be empty") String surname,
        @Email(message = "Advisor email should be valid") String email,
        @NotEmpty(message = "Advisor password should not be empty") String password
) {
}
