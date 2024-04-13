package com.ahmadabbas.filetracking.backend.document.base.repository;

import com.ahmadabbas.filetracking.backend.document.base.view.DocumentStudentIdView;
import com.blazebit.persistence.spring.data.repository.EntityViewRepository;

import java.util.Optional;
import java.util.UUID;

public interface DocumentStudentIdViewRepository extends EntityViewRepository<DocumentStudentIdView, UUID> {
    Optional<DocumentStudentIdView> findById(UUID id);
}