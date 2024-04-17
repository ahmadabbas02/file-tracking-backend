package com.ahmadabbas.filetracking.backend.document.base.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class BaseDocumentAddRequest {
    @JsonProperty
    @NotEmpty(message = "document title should not be empty")
    private final String title;
    @JsonProperty
    private final String description;
    @JsonProperty
    @NotNull
    private final Long categoryId;

    public BaseDocumentAddRequest(String title, String description, Long categoryId) {
        this.title = title;
        this.description = description;
        this.categoryId = categoryId;
    }

    public String title() {
        return title.trim();
    }

    public String description() {
        return description != null ? description.trim() : "";
    }

    public Long categoryId() {
        return categoryId;
    }
}
