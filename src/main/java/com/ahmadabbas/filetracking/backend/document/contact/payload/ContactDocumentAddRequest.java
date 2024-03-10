package com.ahmadabbas.filetracking.backend.document.contact.payload;

import jakarta.validation.constraints.Email;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;


public record ContactDocumentAddRequest(String title, String description, @Email String email, String phoneNumber,
                                        String emergencyName, String emergencyPhoneNumber) implements Serializable {
    public ContactDocumentAddRequest {
        if (title == null || title.isBlank()) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate localDate = LocalDate.now(ZoneId.of("Europe/Athens"));
            title = "Contact Form " + localDate.format(formatter);
        }
    }
}