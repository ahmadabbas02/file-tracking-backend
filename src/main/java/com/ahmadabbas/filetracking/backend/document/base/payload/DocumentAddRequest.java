package com.ahmadabbas.filetracking.backend.document.base.payload;

import jakarta.validation.constraints.NotEmpty;


public record DocumentAddRequest(@NotEmpty String title, String description, @NotEmpty String studentId,
                                 Long parentCategoryId, @NotEmpty Long categoryId) {
    public DocumentAddRequest {
        if (parentCategoryId == null) parentCategoryId = -1L;
    }
}
