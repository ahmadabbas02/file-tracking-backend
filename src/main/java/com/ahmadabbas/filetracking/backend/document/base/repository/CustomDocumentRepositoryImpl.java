package com.ahmadabbas.filetracking.backend.document.base.repository;

import com.ahmadabbas.filetracking.backend.document.base.Document;
import com.ahmadabbas.filetracking.backend.document.base.views.DocumentWithStudentIdView;
import com.ahmadabbas.filetracking.backend.document.base.views.DocumentWithStudentView;
import com.blazebit.persistence.CriteriaBuilder;
import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.view.EntityViewManager;
import com.blazebit.persistence.view.EntityViewSetting;
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
    private final EntityViewManager evm;
    private final CriteriaBuilderFactory criteriaBuilderFactory;

    @Override
    public Optional<Document> getDocumentById(UUID id) {
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
    public Optional<DocumentWithStudentView> getDocumentWithStudentViewById(UUID id) {
        CriteriaBuilder<Document> criteriaBuilder = criteriaBuilderFactory
                .create(entityManager, Document.class)
                .where("id").eq().value(id);
        CriteriaBuilder<DocumentWithStudentView> baseDocumentViewCriteriaBuilder =
                evm.applySetting(EntityViewSetting.create(DocumentWithStudentView.class), criteriaBuilder);
        try {
            return Optional.ofNullable(baseDocumentViewCriteriaBuilder.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<DocumentWithStudentIdView> getDocumentWithStudentIdView(UUID uuid) {
        CriteriaBuilder<Document> criteriaBuilder = criteriaBuilderFactory
                .create(entityManager, Document.class)
                .where("id").eq().value(uuid);
        CriteriaBuilder<DocumentWithStudentIdView> baseDocumentViewCriteriaBuilder =
                evm.applySetting(EntityViewSetting.create(DocumentWithStudentIdView.class), criteriaBuilder);
        try {
            return Optional.ofNullable(baseDocumentViewCriteriaBuilder.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public Page<DocumentWithStudentView> findAllDocuments(String studentId,
                                                          List<String> studentIds,
                                                          List<Long> categoryIds,
                                                          String searchQuery,
                                                          Pageable pageable) {
        CriteriaBuilder<Document> criteriaBuilder = criteriaBuilderFactory
                .create(entityManager, Document.class);
        CriteriaBuilder<DocumentWithStudentView> documentViewCriteriaBuilder
                = evm.applySetting(EntityViewSetting.create(DocumentWithStudentView.class), criteriaBuilder);
        if (!studentId.equals("-1")) {
            studentId = studentId + "%";
            documentViewCriteriaBuilder.where("student.id").like().value(studentId).noEscape();
        } else if (!studentIds.isEmpty()) {
            documentViewCriteriaBuilder.where("student.id").in(studentIds);
        }
        if (!categoryIds.isEmpty()) {
            documentViewCriteriaBuilder.where("category.categoryId").in(categoryIds);
        }
        if (!searchQuery.isBlank()) {
            searchQuery = "%" + searchQuery + "%";
            documentViewCriteriaBuilder
                    .whereOr()
                    .where("title").like(false).value(searchQuery).noEscape()
                    .where("description").like(false).value(searchQuery).noEscape()
                    .endOr();
        }
        return getOrderedPage(
                documentViewCriteriaBuilder.page(
                        (int) pageable.getOffset(),
                        pageable.getPageSize()
                ),
                pageable
        );
    }

}
