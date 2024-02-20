package com.ahmadabbas.filetracking.backend.student.payload;

import com.ahmadabbas.filetracking.backend.student.Student;

import java.io.Serializable;

/**
 * DTO for {@link Student}
 */
public record StudentDto(String id, String name, String department, Short year,
                         String picture) implements Serializable {
}