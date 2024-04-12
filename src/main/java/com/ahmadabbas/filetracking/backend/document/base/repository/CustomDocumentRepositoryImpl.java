package com.ahmadabbas.filetracking.backend.document.base.repository;

import com.ahmadabbas.filetracking.backend.document.base.Document;
import com.blazebit.persistence.CriteriaBuilder;
import com.blazebit.persistence.CriteriaBuilderFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.ahmadabbas.filetracking.backend.util.PagingUtils.getOrderedPage;

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
        try {
            return Optional.ofNullable(criteriaBuilder.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public Page<Document> findAllDocuments(String studentId,
                                           List<String> studentIds,
                                           List<Long> categoryIds,
                                           String searchQuery,
                                           Pageable pageable) {
        CriteriaBuilder<Document> criteriaBuilder = criteriaBuilderFactory
                .create(entityManager, Document.class)
                .fetch("student", "student.user", "student.user.roles");
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
        return getOrderedPage(
                criteriaBuilder.page(
                        (int) pageable.getOffset(),
                        pageable.getPageSize()
                ),
                pageable
        );
    }

}
