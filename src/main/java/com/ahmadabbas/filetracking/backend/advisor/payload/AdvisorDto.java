package com.ahmadabbas.filetracking.backend.advisor.payload;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record AdvisorDto(
        String id,
        String fullName,
        String firstName,
        String lastName
) implements Serializable {
}