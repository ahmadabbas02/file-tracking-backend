package com.ahmadabbas.filetracking.backend.document.payload;

import com.ahmadabbas.filetracking.backend.document.base.Document;

import java.io.Serializable;

/**
 * DTO for {@link Document}
 */
public record DocumentDto(String id, String title, String description, Long categoryParentId,
                          Long categoryId, String categoryName, String studentId, String studentName,
                          String studentDepartment, Short studentYear, String studentPicture) implements Serializable {
}