package com.ahmadabbas.filetracking.backend.advisor.payload;

import com.ahmadabbas.filetracking.backend.advisor.Advisor;

import java.io.Serializable;

/**
 * DTO for {@link Advisor}
 */
public record AdvisorDto(String id, String name, String firstName, String lastName) implements Serializable {
}