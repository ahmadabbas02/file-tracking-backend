package com.ahmadabbas.filetracking.backend.document.base.payload;

import jakarta.validation.constraints.NotEmpty;

import java.util.UUID;

public record DocumentModifyCategoryRequest(@NotEmpty UUID uuid, @NotEmpty Long categoryId, Long parentCategoryId) {
    public DocumentModifyCategoryRequest {
        if (parentCategoryId == null) parentCategoryId = -1L;
    }
}
