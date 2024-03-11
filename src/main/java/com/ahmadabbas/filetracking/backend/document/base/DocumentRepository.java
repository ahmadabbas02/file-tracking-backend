package com.ahmadabbas.filetracking.backend.document.base;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface DocumentRepository extends JpaRepository<Document, UUID> {

    @Query("""
            select d from Document d
            inner join Student s
            on s.id = d.student.id
            where (d.category.categoryId in :categoryIds and d.category.parentCategoryId in :parentCategoryIds)
            """)
    Page<Document> findByHavingCategoryIds(
            List<Long> categoryIds,
            List<Long> parentCategoryIds,
            Pageable pageable
    );

    @Query("""
            select d from Document d
            inner join Student s
            on s.id = d.student.id
            where (d.category.categoryId in :categoryIds and d.category.parentCategoryId in :parentCategoryIds)
            and d.student.id in :studentIds
            """)
    Page<Document> findByHavingCategoryIds(
            List<Long> categoryIds,
            List<Long> parentCategoryIds,
            Pageable pageable,
            List<String> studentIds
    );

    @Query("""
            select d from Document d
            inner join Student s
            on s.id = d.student.id
            where d.student.id = :id
            and d.category.categoryId in :categoryIds
            """)
    Page<Document> findByStudentIdHavingCategoryIds(
            String id,
            List<Long> categoryIds,
            Pageable pageable
    );

    @Query("""
            select d from Document d
            inner join Student s
            on s.id = d.student.id
            where d.student.id = :id
            and (d.category.categoryId in :categoryIds and d.category.parentCategoryId in :parentCategoryIds)
            """)
    Page<Document> findByStudentIdHavingCategoryIds(
            String id,
            List<Long> categoryIds,
            List<Long> parentCategoryIds,
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