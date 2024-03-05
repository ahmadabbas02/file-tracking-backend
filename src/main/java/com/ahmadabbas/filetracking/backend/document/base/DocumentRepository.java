package com.ahmadabbas.filetracking.backend.document.base;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.UUID;

public interface DocumentRepository extends JpaRepository<Document, UUID> {
    @Query("""
            select d from Document d
            inner join Student s
            on s.id = d.student.id
            where d.student.id = ?1
            """)
    Page<Document> findByStudentId(String id, Pageable pageable);

    @Query("""
            select d from Document d
            inner join Student s
            on s.id = d.student.id
            where d.category.categoryId = :categoryId
            and d.category.parentCategoryId = :parentCategoryId
            """)
    Page<Document> findByHavingCategoryIds(
            Long categoryId,
            Long parentCategoryId,
            Pageable pageable
    );

    @Query("""
            select d from Document d
            inner join Student s
            on s.id = d.student.id
            where d.student.id = :id and (d.category.categoryId in :categoryIds or d.category.parentCategoryId in :categoryIds)
            """)
    Page<Document> findByStudentIdHavingCategoryIds(
            String id,
            Collection<Long> categoryIds,
            Pageable pageable
    );

    @Query("""
            select d from Document d
            inner join Student s
            on s.id = d.student.id
            where d.student.id = :id
            and d.category.categoryId = :categoryId
            and d.category.parentCategoryId = :parentCategoryId
            """)
    Page<Document> findByStudentIdHavingCategoryIds(
            String id,
            Long categoryId,
            Long parentCategoryId,
            Pageable pageable
    );


}