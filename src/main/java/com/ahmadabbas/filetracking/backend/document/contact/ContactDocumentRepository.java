package com.ahmadabbas.filetracking.backend.document.contact;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ContactDocumentRepository extends JpaRepository<ContactDocument, Long> {
    @Query("""
            select c from ContactDocument c
            inner join Student s
            on c.student.id = s.id
            where s.id = :id
            """)
    Optional<ContactDocument> findByStudentId(String id);

    @Query("""
            select c from ContactDocument c
            inner join Student s
            on c.student.id = s.id
            where s.id = :id
            order by c.uploadedAt desc
            """)
    Page<ContactDocument> findAllByStudentId(String id, Pageable pageable);

}