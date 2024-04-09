package com.ahmadabbas.filetracking.backend.document.internship.payload;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;

public record InternshipAddRequest(
        @NotEmpty(message = "Internship title should not be empty") String title,
        String description,
        @Positive(message = "number of working days should be positive")
        @Max(value = 40, message = "maximum number of working days is 40") int numberOfWorkingDays,
        @NotEmpty(message = "Internship studentId should not be empty") String studentId
) {
    public InternshipAddRequest {
        title = title.trim();
        if (description == null) {
            description = "";
        } else {
            description = description.trim();
        }
    }
}
