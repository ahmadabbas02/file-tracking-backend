package com.ahmadabbas.filetracking.backend.student;

import com.ahmadabbas.filetracking.backend.advisor.payload.AdvisorDto;
import com.ahmadabbas.filetracking.backend.user.UserDto;

import java.io.Serializable;
import java.util.Date;

/**
 * DTO for {@link Student}
 */
public record StudentDto(String id, String department, Short year, String picture, UserDto user, AdvisorDto advisor,
                         Date createdAt) implements Serializable {
}