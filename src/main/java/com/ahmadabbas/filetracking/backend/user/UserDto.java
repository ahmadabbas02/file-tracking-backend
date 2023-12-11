package com.ahmadabbas.filetracking.backend.user;

import java.io.Serializable;

/**
 * DTO for {@link User}
 */
public record UserDto(Long id, String name, String email, Role role) implements Serializable {
}