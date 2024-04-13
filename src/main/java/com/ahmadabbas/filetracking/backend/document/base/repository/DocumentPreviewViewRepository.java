package com.ahmadabbas.filetracking.backend.document.base.repository;

import com.ahmadabbas.filetracking.backend.document.base.view.DocumentPreviewView;
import com.blazebit.persistence.spring.data.repository.EntityViewRepository;

import java.util.Optional;
import java.util.UUID;

public interface DocumentPreviewViewRepository extends EntityViewRepository<DocumentPreviewView, UUID> {
    Optional<DocumentPreviewView> findById(UUID id);
}