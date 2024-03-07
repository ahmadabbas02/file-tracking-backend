package com.ahmadabbas.filetracking.backend.document.petition;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PetitionDocumentRepository extends JpaRepository<PetitionDocument, UUID> {
}