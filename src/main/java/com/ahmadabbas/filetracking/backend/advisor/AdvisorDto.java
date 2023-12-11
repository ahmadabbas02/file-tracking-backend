package com.ahmadabbas.filetracking.backend.advisor;

import com.ahmadabbas.filetracking.backend.user.UserDto;

import java.io.Serializable;
import java.util.Date;

/**
 * DTO for {@link Advisor}
 */
public record AdvisorDto(String id, UserDto user, Date createdAt) implements Serializable {
}