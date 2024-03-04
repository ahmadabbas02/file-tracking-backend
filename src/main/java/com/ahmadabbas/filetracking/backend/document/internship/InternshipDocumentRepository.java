package com.ahmadabbas.filetracking.backend.document.internship;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface InternshipDocumentRepository extends JpaRepository<InternshipDocument, UUID> {
    @Query("""
            select i from InternshipDocument i
            join fetch Student s
            on s.id = i.student.id
            where i.student.id = :studentId
            """)
    List<InternshipDocument> findAllByStudentId(String studentId);

}