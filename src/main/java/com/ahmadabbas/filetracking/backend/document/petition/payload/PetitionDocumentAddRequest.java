package com.ahmadabbas.filetracking.backend.document.petition.payload;

import com.ahmadabbas.filetracking.backend.document.base.payload.BaseDocumentAddRequest;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;

public class PetitionDocumentAddRequest extends BaseDocumentAddRequest {
    @JsonProperty
    @NotEmpty(message = "Petition subject should not be empty")
    private final String subject;
    @JsonProperty
    @Email(message = "Petition email should be valid")
    private final String email;
    @JsonProperty
    @NotEmpty(message = "Petition phone number should not be empty")
    private final String phoneNumber;
    @JsonProperty
    @NotEmpty(message = "Petition reasoning should not be empty")
    private final String reasoning;


    public PetitionDocumentAddRequest(String title, String description, Long categoryId, String subject, String email, String phoneNumber, String reasoning) {
        super(title, description, categoryId);
        this.subject = subject;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.reasoning = reasoning;
    }

    public String subject() {
        return subject;
    }

    public String email() {
        return email.trim().toLowerCase();
    }

    public String phoneNumber() {
        return phoneNumber;
    }

    public String reasoning() {
        return reasoning;
    }

}