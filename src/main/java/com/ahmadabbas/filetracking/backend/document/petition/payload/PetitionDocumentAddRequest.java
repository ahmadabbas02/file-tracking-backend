package com.ahmadabbas.filetracking.backend.document.petition.payload;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;

public record PetitionDocumentAddRequest(
        @NotEmpty String title,
        @NotEmpty String description,
        @NotEmpty String subject,
        @Email String email,
        @NotEmpty String phoneNumber,
        @NotEmpty String reasoning
) {
}
