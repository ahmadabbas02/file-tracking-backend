package com.ahmadabbas.filetracking.backend.document.base.payload;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record DocumentModifyCategoryRequest(
        @NotNull(message = "uuid should not be empty") UUID uuid,
        @NotNull(message = "categoryId should not be empty") Long categoryId
) {
}
