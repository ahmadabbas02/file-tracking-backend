package com.ahmadabbas.filetracking.backend.validator;

import com.ahmadabbas.filetracking.backend.user.Role;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;

public class RoleTypeSubSetValidator implements ConstraintValidator<RoleTypeSubSet, Role> {
    private Role[] roles;

    @Override
    public void initialize(RoleTypeSubSet constraint) {
        this.roles = constraint.anyOf();
    }

    @Override
    public boolean isValid(Role value, ConstraintValidatorContext context) {
        return value == null || Arrays.asList(roles).contains(value);
    }
}