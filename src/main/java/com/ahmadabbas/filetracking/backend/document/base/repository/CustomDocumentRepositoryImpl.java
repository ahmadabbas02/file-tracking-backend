package com.ahmadabbas.filetracking.backend.document.base.repository;

import com.ahmadabbas.filetracking.backend.document.base.Document;
import com.ahmadabbas.filetracking.backend.document.base.view.DocumentPreviewView;
import com.ahmadabbas.filetracking.backend.document.base.view.DocumentStudentView;
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
    public Page<DocumentStudentView> findAllDocuments(String studentId,
                                                      List<String> studentIds,
                                                      List<Long> categoryIds,
                                                      String searchQuery,
                                                      Pageable pageable) {
        CriteriaBuilder<Document> criteriaBuilder = criteriaBuilderFactory
                .create(entityManager, Document.class);
        CriteriaBuilder<DocumentStudentView> documentViewCriteriaBuilder
                = evm.applySetting(EntityViewSetting.create(DocumentStudentView.class), criteriaBuilder);
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

    @Override
    public List<DocumentPreviewView> findDocumentPreviews(List<UUID> uuids) {
        CriteriaBuilder<Document> criteriaBuilder = criteriaBuilderFactory
                .create(entityManager, Document.class)
                .where("id").in(uuids)
                .distinct();
        var documentCategoryViewCriteriaBuilder
                = evm.applySetting(EntityViewSetting.create(DocumentPreviewView.class), criteriaBuilder);
        return documentCategoryViewCriteriaBuilder.getResultList();
    }
}
