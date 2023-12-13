package com.ahmadabbas.filetracking.backend.document.category;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {

    private final CategoryRepository categoryRepository;


    public CategoryController(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @GetMapping
    public List<Category> getAllMainCategories() {
        return categoryRepository.findByParentCategoryId(-1L);
    }

    @GetMapping("{parentId}")
    public List<Category> getAllSubCategories(@PathVariable Long parentId) {
        return categoryRepository.findByParentCategoryId(parentId);
    }

    @GetMapping("full")
    public List<FullCategoryResponse> getAllCategories() {
        List<Category> parentCategories = categoryRepository.findByParentCategoryId(-1L);
        List<FullCategoryResponse> responses = new LinkedList<>();
        for (var parent : parentCategories) {
            List<Category> childrenCategories = categoryRepository.findByParentCategoryId(parent.getCategoryId());
            if (!childrenCategories.isEmpty()) {
                responses.add(new FullCategoryResponse(parent, childrenCategories));
                continue;
            }
            responses.add(new FullCategoryResponse(parent, Collections.emptyList()));
        }
        return responses;
    }
}
