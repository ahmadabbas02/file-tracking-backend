package com.ahmadabbas.filetracking.backend.document.contact.payload;

import com.ahmadabbas.filetracking.backend.document.contact.ContactDocument;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for {@link ContactDocument}
 */
public record ContactDocumentDto(UUID id, String title, String description, Long categoryParentCategoryId,
                                 Long categoryCategoryId, String categoryName, String studentId,
                                 String studentDepartment, Short studentYear, String studentPicture,
                                 String studentName, LocalDateTime uploadedAt, String email,
                                 String phoneNumber, String emergencyName,
                                 String emergencyPhoneNumber) implements Serializable {
}