package com.ahmadabbas.filetracking.backend.user.payload;

import com.ahmadabbas.filetracking.backend.user.User;

import java.io.Serializable;
import java.util.Map;

/**
 * DTO for {@link User}
 */
public record UserDto(
        Long id,
        String firstName,
        String lastName,
        String fullName,
        String email,
        String phoneNumber,
        String picture,
        boolean enabled,
        boolean credentialsExpired,
        Map<String, Object> roles
) implements Serializable {
}