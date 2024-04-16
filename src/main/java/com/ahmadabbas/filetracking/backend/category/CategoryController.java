package com.ahmadabbas.filetracking.backend.category;

import com.ahmadabbas.filetracking.backend.category.payload.AddCategoryRequest;
import com.ahmadabbas.filetracking.backend.category.payload.CategoryPermissionRequestDto;
import com.ahmadabbas.filetracking.backend.category.payload.FullCategoryPermissionResponse;
import com.ahmadabbas.filetracking.backend.category.payload.FullCategoryResponse;
import com.ahmadabbas.filetracking.backend.user.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/categories")
@Tag(name = "Category")
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(
            summary = "Add category"
    )
    @PostMapping("")
    public ResponseEntity<Category> addCategory(@RequestBody AddCategoryRequest category,
                                                @AuthenticationPrincipal UserPrincipal principal) {
        return new ResponseEntity<>(categoryService.createCategory(category, principal.getUserEntity()), HttpStatus.CREATED);
    }

    @Operation(
            summary = "Get All Categories",
            description = "Returns a list of all categories"
    )
    @GetMapping("")
    public ResponseEntity<List<?>> getAllCategories(
            @RequestParam(value = "parents_only", required = false, defaultValue = "false") boolean parentsOnly,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        if (parentsOnly) {
            List<Category> allParentCategories = categoryService.getAllParentCategories(principal.getUserEntity());
            return ResponseEntity.ok(allParentCategories);
        }
        List<FullCategoryResponse> allCategories = categoryService.getAllCategories(principal.getUserEntity());
        return ResponseEntity.ok(allCategories);
    }

    @Operation(
            summary = "Get all children categories",
            description = "Returns a list of all categories with `parentId` as their parent category."
    )
    @GetMapping("/{parentId}")
    public ResponseEntity<List<Category>> getAllChildrenCategories(@PathVariable Long parentId,
                                                                   @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(categoryService.getAllChildrenCategories(parentId, principal.getUserEntity()));
    }

    @Operation(
            summary = "Get all category permissions",
            description = "Returns all the category visibility permissions."
    )
    @GetMapping("/permissions")
    public ResponseEntity<List<FullCategoryPermissionResponse>> getAllCategoryPermissions(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return ResponseEntity.ok(categoryService.getAllCategoryPermissions(principal.getUserEntity()));
    }

    @Operation(
            summary = "Update category permissions",
            description = "Add/delete role view permissions for specific categories."
    )
    @PostMapping("/permissions/update")
    public ResponseEntity<FullCategoryPermissionResponse> updateCategoryPerms(
            @Valid @RequestBody CategoryPermissionRequestDto requestDto,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return ResponseEntity.ok(categoryService.updateCategoryPermission(requestDto, principal.getUserEntity()));
    }
}
