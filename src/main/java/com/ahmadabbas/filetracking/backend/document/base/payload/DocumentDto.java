package com.ahmadabbas.filetracking.backend.document.base.payload;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class DocumentDto implements Serializable {
    String id;
    String title;
    String description;
    Long categoryParentId;
    Long categoryId;
    String categoryName;
    String studentId;
    String studentFirstName;
    String studentLastName;
    String studentFullName;
    String studentProgram;
    Short studentYear;
    String studentPicture;
    LocalDateTime uploadedAt;
}