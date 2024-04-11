package com.ahmadabbas.filetracking.backend.document.contact.payload;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;

import java.io.Serializable;


public record ContactDocumentAddRequest(
        @NotEmpty(message = "Contact form title should not be empty") String title,
        String description,
        @Email(message = "Contact form email should be valid") String email,
        @NotEmpty(message = "Contact form phone number should not be empty") String phoneNumber,
        @NotEmpty(message = "Contact form home number should not be empty") String homeNumber,
        @NotEmpty(message = "Contact form emergency name should not be empty") String emergencyName,
        @NotEmpty(message = "Contact form phone number description should not be empty") String emergencyPhoneNumber
) implements Serializable {
    public ContactDocumentAddRequest {
        title = title.trim();
        if (description == null) {
            description = "";
        } else {
            description = description.trim();
        }
        email = email.toLowerCase();
    }
}