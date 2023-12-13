package com.ahmadabbas.filetracking.backend.document.category;

import java.util.List;

public record FullCategoryResponse(
        Category mainCategory,
        List<Category> subCategories
) {
}
