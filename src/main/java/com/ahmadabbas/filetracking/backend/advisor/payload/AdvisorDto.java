package com.ahmadabbas.filetracking.backend.advisor.payload;

import com.ahmadabbas.filetracking.backend.advisor.Advisor;
import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link Advisor}
 */
public record AdvisorDto(String id, String name) implements Serializable {
}