package com.ahmadabbas.filetracking.backend.document.base;

import com.ahmadabbas.filetracking.backend.student.StudentDto;

import java.io.Serializable;
import java.util.Date;

/**
 * DTO for {@link Document}
 */
public record DocumentDto(Long id, String title, String path, Long categoryParentCategoryId, Long categoryCategoryId,
                          String categoryName, StudentDto student, Date uploadedAt) implements Serializable {
}