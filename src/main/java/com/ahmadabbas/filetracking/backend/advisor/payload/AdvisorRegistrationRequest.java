package com.ahmadabbas.filetracking.backend.advisor.payload;

public record AdvisorRegistrationRequest(
        String name,
        String email,
        String password
) {
}
