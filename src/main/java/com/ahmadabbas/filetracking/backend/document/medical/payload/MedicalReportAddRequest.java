package com.ahmadabbas.filetracking.backend.document.medical.payload;


import jakarta.validation.constraints.NotEmpty;

public record MedicalReportAddRequest(
        @NotEmpty(message = "Medical report title should not be empty") String title,
        String description,
        @NotEmpty(message = "Medical report dateOfAbsence should not be empty") String dateOfAbsence
) {
    public MedicalReportAddRequest {
        title = title.trim();
        if (description == null) {
            description = "";
        } else {
            description = description.trim();
        }
    }
}
