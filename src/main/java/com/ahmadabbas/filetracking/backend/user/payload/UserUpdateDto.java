package com.ahmadabbas.filetracking.backend.user.payload;

import java.io.Serializable;

public record UserUpdateDto(
        String firstName,
        String lastName,
        String email,
        String password,
        String picture,
        String phoneNumber,
        boolean enabled,
        boolean credentialsExpired) implements Serializable {
}