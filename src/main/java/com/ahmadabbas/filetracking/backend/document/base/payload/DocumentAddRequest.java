package com.ahmadabbas.filetracking.backend.document.base.payload;

import jakarta.validation.constraints.NotEmpty;


public record DocumentAddRequest(
        @NotEmpty(message = "Document title should not be empty") String title,
        @NotEmpty(message = "Document description should not be empty") String description,
        @NotEmpty(message = "studentId should not be empty") String studentId,
        Long parentCategoryId,
        @NotEmpty(message = "categoryId should not be empty") Long categoryId
) {
    public DocumentAddRequest {
        if (parentCategoryId == null) parentCategoryId = -1L;
    }
}
