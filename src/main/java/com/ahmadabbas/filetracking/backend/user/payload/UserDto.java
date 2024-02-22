package com.ahmadabbas.filetracking.backend.user.payload;

import com.ahmadabbas.filetracking.backend.user.Role;
import com.ahmadabbas.filetracking.backend.user.User;

import java.io.Serializable;
import java.util.Set;

/**
 * DTO for {@link User}
 */
public record UserDto(Long id, String name, String email, Set<Role> roles) implements Serializable {
}