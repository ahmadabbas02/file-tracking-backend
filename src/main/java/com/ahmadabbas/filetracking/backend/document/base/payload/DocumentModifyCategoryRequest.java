package com.ahmadabbas.filetracking.backend.document.base.payload;

import jakarta.validation.constraints.NotEmpty;

import java.util.UUID;

public record DocumentModifyCategoryRequest(
        @NotEmpty(message = "uuid should not be empty") UUID uuid,
        @NotEmpty(message = "categoryId should not be empty") Long categoryId,
        Long parentCategoryId
) {
    public DocumentModifyCategoryRequest {
        if (parentCategoryId == null) parentCategoryId = -1L;
    }
}
