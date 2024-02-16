package com.ahmadabbas.filetracking.backend.category.payload;

import com.ahmadabbas.filetracking.backend.category.Category;

import java.util.List;

public record FullCategoryResponse(
        Category mainCategory,
        List<Category> subCategories
) {
}
