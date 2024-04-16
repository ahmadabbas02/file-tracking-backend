package com.ahmadabbas.filetracking.backend.document.base.payload;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class DocumentAddRequest extends BaseDocumentAddRequest {
    @NotNull(message = "categoryId should not be empty")
    private final Long categoryId;
    @NotEmpty(message = "studentId should not be empty")
    private final String studentId;

    public DocumentAddRequest(String title, String description, Long categoryId, String studentId) {
        super(title, description);
        this.categoryId = categoryId;
        this.studentId = studentId;
    }

    public String studentId() {
        return studentId;
    }

    public Long categoryId() {
        return categoryId;
    }
}
