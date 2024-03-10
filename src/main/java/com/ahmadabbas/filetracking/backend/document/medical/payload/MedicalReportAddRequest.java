package com.ahmadabbas.filetracking.backend.document.medical.payload;


import jakarta.validation.constraints.NotEmpty;

public record MedicalReportAddRequest(
        @NotEmpty(message = "Medical report title should not be empty") String title,
        @NotEmpty(message = "Medical report description should not be empty") String description,
        @NotEmpty(message = "Medical report dateOfAbsence should not be empty") String dateOfAbsence
) {
}
