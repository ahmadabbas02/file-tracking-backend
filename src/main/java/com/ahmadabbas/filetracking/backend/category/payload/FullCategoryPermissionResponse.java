package com.ahmadabbas.filetracking.backend.category.payload;

import com.ahmadabbas.filetracking.backend.user.Role;

import java.util.List;

public record FullCategoryPermissionResponse(
        Long categoryId,
        String name,
        List<Role> permittedRoles
) {
}
