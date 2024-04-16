package com.ahmadabbas.filetracking.backend.document.base.payload;

import jakarta.validation.constraints.NotEmpty;

public class DocumentAddRequest extends BaseDocumentAddRequest {
    @NotEmpty(message = "studentId should not be empty")
    private final String studentId;

    public DocumentAddRequest(String title, String description, String studentId) {
        super(title, description);
        this.studentId = studentId;
    }

    public String studentId() {
        return studentId;
    }
}
