package com.ahmadabbas.filetracking.backend.advisor.payload;

import com.ahmadabbas.filetracking.backend.advisor.Advisor;

import java.io.Serializable;

/**
 * DTO for {@link Advisor}
 */
public record AdvisorUserDto(String id) implements Serializable {
}