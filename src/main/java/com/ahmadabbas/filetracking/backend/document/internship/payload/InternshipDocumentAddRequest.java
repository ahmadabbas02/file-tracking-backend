package com.ahmadabbas.filetracking.backend.document.internship.payload;

import com.ahmadabbas.filetracking.backend.document.base.payload.DocumentAddRequest;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Positive;

public final class InternshipDocumentAddRequest extends DocumentAddRequest {
    @JsonProperty
    @Positive(message = "number of working days should be positive")
    @Max(value = 40, message = "maximum number of working days is 40")
    private final int numberOfWorkingDays;

    public InternshipDocumentAddRequest(String title, String description, String studentId, int numberOfWorkingDays) {
        super(title, description, studentId);
        this.numberOfWorkingDays = numberOfWorkingDays;
    }

    public int numberOfWorkingDays() {
        return numberOfWorkingDays;
    }
}
