package com.ahmadabbas.filetracking.backend.document.base;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRepository extends JpaRepository<Document, Long> {
    Page<Document> findByStudent_Id(String id, Pageable pageable);

}