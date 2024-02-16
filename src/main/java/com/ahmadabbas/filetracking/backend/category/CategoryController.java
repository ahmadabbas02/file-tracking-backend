package com.ahmadabbas.filetracking.backend.category;

import com.ahmadabbas.filetracking.backend.category.payload.CategoryPermissionRequestDto;
import com.ahmadabbas.filetracking.backend.category.payload.FullCategoryResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(
            summary = "Add category"
    )
    @PostMapping
    public ResponseEntity<Category> addCategory(@RequestBody Category category) {
        return new ResponseEntity<>(categoryService.createCategory(category), HttpStatus.CREATED);
    }

    @Operation(
            summary = "Get All Categories",
            description = "Returns a list of all categories"
    )
    @GetMapping
    public ResponseEntity<List> getAllCategories(
            @RequestParam(value = "parents_only", required = false, defaultValue = "false") boolean parentsOnly,
            Authentication authentication
    ) {
        if (parentsOnly) {
            List<Category> allParentCategories = categoryService.getAllParentCategories(authentication);
            return ResponseEntity.ok(allParentCategories);
        }
        List<FullCategoryResponse> allCategories = categoryService.getAllCategories2(authentication);
        return ResponseEntity.ok(allCategories);
    }

    @Operation(
            summary = "Get all children categories",
            description = "Returns a list of all categories with `parentId` as their parent category."
    )
    @GetMapping("{parentId}")
    public ResponseEntity<List<Category>> getAllChildrenCategories(@PathVariable Long parentId) {
        return ResponseEntity.ok(categoryService.getAllChildrenCategories(parentId));
    }

    @Operation(
            summary = "Update category permissions",
            description = "Add/delete role view permissions for specific categories."
    )
    @PostMapping("/permissions/update")
    @PreAuthorize("hasRole(T(com.ahmadabbas.filetracking.backend.user.Role).ADMINISTRATOR)")
    public ResponseEntity<Category> updateCategoryPerms(@Valid @RequestBody CategoryPermissionRequestDto requestDto) {
        return ResponseEntity.ok(categoryService.updateCategoryPermission(requestDto));
    }
}
