package com.ahmadabbas.filetracking.backend.document.base.payload;

import jakarta.validation.constraints.NotEmpty;


public record DocumentAddRequest(
        @NotEmpty(message = "Document title should not be empty") String title,
        String description,
        @NotEmpty(message = "studentId should not be empty") String studentId,
        @NotEmpty(message = "categoryId should not be empty") Long categoryId
) {
    public DocumentAddRequest {
        title = title.trim();
        if (description == null) {
            description = "";
        } else {
            description = description.trim();
        }
    }
}
