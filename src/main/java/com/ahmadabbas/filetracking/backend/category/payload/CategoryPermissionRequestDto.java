package com.ahmadabbas.filetracking.backend.category.payload;

import com.ahmadabbas.filetracking.backend.user.Role;
import com.ahmadabbas.filetracking.backend.validator.RoleTypeSubSet;
import jakarta.validation.constraints.Positive;

public record CategoryPermissionRequestDto(
        boolean delete,
        @Positive Long categoryId,
        @RoleTypeSubSet(anyOf = {Role.STUDENT, Role.ADVISOR, Role.SECRETARY, Role.CHAIR}) Role role
) {
}
