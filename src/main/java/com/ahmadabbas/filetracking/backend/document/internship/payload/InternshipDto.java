package com.ahmadabbas.filetracking.backend.document.internship.payload;

import com.ahmadabbas.filetracking.backend.document.internship.InternshipDocument;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for {@link InternshipDocument}
 */
public record InternshipDto(UUID id, String title, String description, Long parentCategoryId,
                            Long categoryId, String categoryName, LocalDateTime uploadedAt,
                            int numberOfWorkingDays) implements Serializable {
}