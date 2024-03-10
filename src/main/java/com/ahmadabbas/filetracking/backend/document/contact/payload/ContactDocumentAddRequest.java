package com.ahmadabbas.filetracking.backend.document.contact.payload;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;

import java.io.Serializable;


public record ContactDocumentAddRequest(
        @NotEmpty(message = "Contact form title should not be empty") String title,
        @NotEmpty(message = "Contact form description should not be empty") String description,
        @Email(message = "Contact form email should be valid") String email,
        @NotEmpty(message = "Contact form phone number should not be empty") String phoneNumber,
        @NotEmpty(message = "Contact form emergency name should not be empty") String emergencyName,
        @NotEmpty(message = "Contact form phone number description should not be empty") String emergencyPhoneNumber
) implements Serializable {
    public ContactDocumentAddRequest {
//        if (title == null || title.isBlank()) {
//            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
//            LocalDate localDate = LocalDate.now(ZoneId.of("Europe/Athens"));
//            title = "Contact Form " + localDate.format(formatter);
//        }
    }
}