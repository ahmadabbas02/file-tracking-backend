package com.ahmadabbas.filetracking.backend.user.payload;

import com.ahmadabbas.filetracking.backend.user.User;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * DTO for {@link User}
 */
public record UserDto(
        Long id,
        String firstName,
        String lastName,
        String name,
        String email,
        String phoneNumber,
//        Set<Role> roles,
        Map<String, Object> roles
) implements Serializable {
}