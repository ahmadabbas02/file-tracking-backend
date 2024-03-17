package com.ahmadabbas.filetracking.backend.document.base.repository;

import com.ahmadabbas.filetracking.backend.document.base.Document;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;

import java.util.*;

public interface DocumentRepository extends JpaRepository<Document, UUID>, CustomDocumentRepository {

    @Query("""
            select d from Document d
            inner join Student s
            on s.id = d.student.id
            where d.category.categoryId in :categoryIds
            """)
    Page<Document> findByHavingCategoryIds(
            List<Long> categoryIds,
            Pageable pageable
    );

    @Query("""
            select d from Document d
            inner join Student s
            on s.id = d.student.id
            where d.category.categoryId in :categoryIds
            and d.student.id in :studentIds
            """)
    Page<Document> findByHavingCategoryIds(
            List<Long> categoryIds,
            Pageable pageable,
            List<String> studentIds
    );

    @Query("""
            select d from Document d
            inner join Student s
            on s.id = d.student.id
            where d.student.id = :studentId
            and d.category.categoryId in :categoryIds
            """)
    Page<Document> findByStudentIdHavingCategoryIds(
            String studentId,
            List<Long> categoryIds,
            Pageable pageable
    );

    @Query("""
            select d from Document d
            inner join Student s
            on s.id = d.student.id
            where d.student.id in :studentIds
            """)
    Page<Document> findAll(Pageable pageable, List<String> studentIds);
}