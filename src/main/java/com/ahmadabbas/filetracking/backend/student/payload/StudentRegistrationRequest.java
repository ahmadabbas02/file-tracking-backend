package com.ahmadabbas.filetracking.backend.student.payload;

public record StudentRegistrationRequest(
        String name,
        String email,
        String password,
        String department,
        Short year,
        String picture,
        String advisorId
) {
}
