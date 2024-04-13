package com.ahmadabbas.filetracking.backend.document.base.view;

import com.ahmadabbas.filetracking.backend.document.base.Document;
import com.blazebit.persistence.view.EntityView;
import com.blazebit.persistence.view.IdMapping;
import com.blazebit.persistence.view.Mapping;

import java.time.LocalDateTime;
import java.util.UUID;

@EntityView(Document.class)
public interface DocumentStudentIdView {
    @IdMapping
    UUID getId();

    String getTitle();

    String getDescription();

    @Mapping("category.parentCategoryId")
    Long getParentCategoryId();

    @Mapping("category.categoryId")
    Long getCategoryId();

    @Mapping("category.name")
    String getCategoryName();

    @Mapping("student.id")
    String getStudentId();

    LocalDateTime getUploadedAt();
}