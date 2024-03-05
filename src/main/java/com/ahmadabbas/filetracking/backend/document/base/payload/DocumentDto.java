package com.ahmadabbas.filetracking.backend.document.base.payload;

import com.ahmadabbas.filetracking.backend.document.base.Document;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for {@link Document}
 */
@Data
public class DocumentDto implements Serializable {
    String id;
    String title;
    String description;
    Long categoryParentId;
    Long categoryId;
    String categoryName;
    String studentId;
    String studentName;
    String studentDepartment;
    Short studentYear;
    String studentPicture;
    LocalDateTime uploadedAt;
}