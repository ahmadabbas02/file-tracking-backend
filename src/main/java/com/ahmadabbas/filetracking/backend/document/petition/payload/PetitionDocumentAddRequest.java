package com.ahmadabbas.filetracking.backend.document.petition.payload;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;

public record PetitionDocumentAddRequest(
        @NotEmpty(message = "Petition title should not be empty") String title,
        @NotEmpty(message = "Petition description not be empty") String description,
        @NotEmpty(message = "Petition subject should not be empty") String subject,
        @Email(message = "Petition email should be valid") String email,
        @NotEmpty(message = "Petition phone number should not be empty") String phoneNumber,
        @NotEmpty(message = "Petition reasoning should not be empty") String reasoning
) {
}
