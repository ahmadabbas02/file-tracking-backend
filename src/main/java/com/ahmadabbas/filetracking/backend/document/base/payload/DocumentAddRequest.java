package com.ahmadabbas.filetracking.backend.document.base.payload;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class DocumentAddRequest extends BaseDocumentAddRequest {
    @NotNull(message = "categoryId should not be empty")
    private final Long categoryId;
    @NotEmpty(message = "studentId should not be empty")
    private final String studentId;

    public DocumentAddRequest(String title, String description, Long categoryId, Long categoryId1, String studentId) {
        super(title, description, categoryId);
        this.categoryId = categoryId1;
        this.studentId = studentId;
    }

    public String studentId() {
        return studentId;
    }

    public Long categoryId() {
        return categoryId;
    }
}
