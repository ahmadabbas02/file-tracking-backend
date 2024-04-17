package com.ahmadabbas.filetracking.backend.document.medical.payload;

import com.ahmadabbas.filetracking.backend.document.base.payload.BaseDocumentAddRequest;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;

public final class MedicalReportAddRequest extends BaseDocumentAddRequest {
    @JsonProperty
    @NotEmpty(message = "Medical report dateOfAbsence should not be empty")
    private final String dateOfAbsence;

    public MedicalReportAddRequest(String title, String description, Long categoryId, String dateOfAbsence) {
        super(title, description, categoryId);
        this.dateOfAbsence = dateOfAbsence;
    }

    public String dateOfAbsence() {
        return dateOfAbsence;
    }
}
