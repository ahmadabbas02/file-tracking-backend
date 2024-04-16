package com.ahmadabbas.filetracking.backend.document.base.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;

public class BaseDocumentAddRequest {
    @JsonProperty
    @NotEmpty(message = "document title should not be empty")
    private final String title;
    @JsonProperty
    private final String description;

    public BaseDocumentAddRequest(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public String title() {
        return title.trim();
    }

    public String description() {
        return description != null ? description.trim() : "";
    }
}
