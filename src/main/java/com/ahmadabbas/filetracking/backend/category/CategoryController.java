package com.ahmadabbas.filetracking.backend.category;

import com.ahmadabbas.filetracking.backend.category.payload.*;
import com.ahmadabbas.filetracking.backend.user.User;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    @PostMapping("")
    public ResponseEntity<Category> addCategory(@RequestBody AddCategoryRequest category, @AuthenticationPrincipal User user) {
        return new ResponseEntity<>(categoryService.createCategory(category, user), HttpStatus.CREATED);
    }

    @Operation(
            summary = "Get All Categories",
            description = "Returns a list of all categories"
    )
    @GetMapping("")
    public ResponseEntity<List<?>> getAllCategories(
            @RequestParam(value = "parents_only", required = false, defaultValue = "false") boolean parentsOnly,
            @AuthenticationPrincipal User user
    ) {
        if (parentsOnly) {
            List<Category> allParentCategories = categoryService.getAllParentCategories(user);
            return ResponseEntity.ok(allParentCategories);
        }
        List<FullCategoryResponse> allCategories = categoryService.getAllCategories(user);
        return ResponseEntity.ok(allCategories);
    }

    @Operation(
            summary = "Get all children categories",
            description = "Returns a list of all categories with `parentId` as their parent category."
    )
    @GetMapping("/{parentId}")
    public ResponseEntity<List<Category>> getAllChildrenCategories(@PathVariable Long parentId, @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(categoryService.getAllChildrenCategories(parentId, user));
    }

    @Operation(
            summary = "Get all category permissions",
            description = "Returns all the category visibility permissions."
    )
    @GetMapping("/permissions")
    public ResponseEntity<List<FullCategoryPermissionResponse>> getAllCategoryPermissions() {
        return ResponseEntity.ok(categoryService.getAllCategoryPermissions());
    }

    @Operation(
            summary = "Update category permissions",
            description = "Add/delete role view permissions for specific categories."
    )
    @PostMapping("/permissions/update")
    public ResponseEntity<FullCategoryPermissionResponse> updateCategoryPerms(@Valid @RequestBody CategoryPermissionRequestDto requestDto,
                                                        @AuthenticationPrincipal User loggedInUser) {
        return ResponseEntity.ok(categoryService.updateCategoryPermission(requestDto, loggedInUser));
    }
}
