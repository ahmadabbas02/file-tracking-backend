package com.ahmadabbas.filetracking.backend.document.base.view;

import com.ahmadabbas.filetracking.backend.document.base.Document;
import com.blazebit.persistence.view.EntityView;
import com.blazebit.persistence.view.IdMapping;
import com.blazebit.persistence.view.Mapping;

import java.util.UUID;

@EntityView(Document.class)
public interface DocumentPreviewView {
    @IdMapping
    UUID getId();

    @Mapping("category.categoryId")
    Long getCategoryId();

    @Mapping("category.name")
    String getCategoryName();

    @Mapping("student.id")
    String getStudentId();

    @Mapping("path")
    String getPath();

    default String getFileName() {
        return getPath().substring(getPath().lastIndexOf('/') + 1);
    }
}