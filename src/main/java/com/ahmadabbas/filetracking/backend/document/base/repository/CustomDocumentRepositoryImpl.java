package com.ahmadabbas.filetracking.backend.document.base.repository;

import com.ahmadabbas.filetracking.backend.document.base.Document;
import com.blazebit.persistence.CriteriaBuilder;
import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.PagedList;
import com.blazebit.persistence.PaginatedCriteriaBuilder;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class CustomDocumentRepositoryImpl implements CustomDocumentRepository {
    @PersistenceContext
    private final EntityManager entityManager;
    private final CriteriaBuilderFactory criteriaBuilderFactory;

    @Override
    public Optional<Document> findOneById(UUID id) {
        CriteriaBuilder<Document> criteriaBuilder = criteriaBuilderFactory
                .create(entityManager, Document.class)
                .where("id").eq().value(id);
        return Optional.ofNullable(criteriaBuilder.getSingleResult());
    }

    @Override
    public Page<Document> findAllDocuments(String studentId,
                                           List<String> studentIds,
                                           List<Long> categoryIds,
                                           String searchQuery,
                                           Pageable pageable) {
        PaginatedCriteriaBuilder<Document> criteriaBuilder = criteriaBuilderFactory
                .create(entityManager, Document.class)
                .page(
                        (int) pageable.getOffset(),
                        pageable.getPageSize()
                );
        if (!studentId.equals("-1")) {
            studentId = studentId + "%";
            criteriaBuilder.where("student.id").like().value(studentId).noEscape();
        } else if (!studentIds.isEmpty()) {
            criteriaBuilder.where("student.id").in(studentIds);
        }
        if (!categoryIds.isEmpty()) {
            criteriaBuilder.where("category.categoryId").in(categoryIds);
        }
        if (!searchQuery.isBlank()) {
            searchQuery = "%" + searchQuery + "%";
            criteriaBuilder
                    .whereOr()
                    .where("title").like(false).value(searchQuery).noEscape()
                    .where("description").like(false).value(searchQuery).noEscape()
                    .endOr();
        }
        return getOrderedDocumentsPage(pageable, criteriaBuilder);
    }

    private Page<Document> getOrderedDocumentsPage(Pageable pageable,
                                                   PaginatedCriteriaBuilder<Document> criteriaBuilder) {
        Map<String, Sort.Direction> sortProperties = pageable.getSort()
                .stream()
                .collect(Collectors.toMap(Sort.Order::getProperty, Sort.Order::getDirection));
        for (var entry : sortProperties.entrySet()) {
            criteriaBuilder.orderBy(entry.getKey(), entry.getValue().equals(Sort.Direction.ASC));
        }
        criteriaBuilder.orderBy("id", true);
        PagedList<Document> resultList = criteriaBuilder.getResultList();
        return new PageImpl<>(resultList, pageable, resultList.getTotalSize());
    }
}
