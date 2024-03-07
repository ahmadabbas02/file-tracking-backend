package com.ahmadabbas.filetracking.backend.document.petition.payload;

public record PetitionDocumentAddRequest(String subject, String email, String phoneNumber, String reasoning) {
}
