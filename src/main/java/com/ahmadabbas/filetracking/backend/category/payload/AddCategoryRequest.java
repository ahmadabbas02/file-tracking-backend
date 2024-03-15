package com.ahmadabbas.filetracking.backend.category.payload;

import jakarta.validation.constraints.NotEmpty;

public record AddCategoryRequest(Long parentCategoryId, @NotEmpty String name) {
    public AddCategoryRequest {
        if (parentCategoryId == null) {
            parentCategoryId = -1L;
        }
    }
}
