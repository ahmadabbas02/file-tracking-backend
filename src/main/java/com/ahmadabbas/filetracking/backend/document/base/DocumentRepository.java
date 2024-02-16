package com.ahmadabbas.filetracking.backend.document.base;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface DocumentRepository extends JpaRepository<Document, UUID> {
    @Query("select d from Document d where d.student.id = ?1")
    Page<Document> findByStudentId(String id, Pageable pageable);

}