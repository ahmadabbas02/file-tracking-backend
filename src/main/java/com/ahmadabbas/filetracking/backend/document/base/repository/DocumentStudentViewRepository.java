package com.ahmadabbas.filetracking.backend.document.base.repository;

import com.ahmadabbas.filetracking.backend.document.base.view.DocumentStudentView;
import com.blazebit.persistence.spring.data.repository.EntityViewRepository;

import java.util.Optional;
import java.util.UUID;

public interface DocumentStudentViewRepository extends EntityViewRepository<DocumentStudentView, UUID> {
    Optional<DocumentStudentView> findById(UUID id);
}