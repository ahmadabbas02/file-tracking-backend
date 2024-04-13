package com.ahmadabbas.filetracking.backend.document.base.views;

import com.ahmadabbas.filetracking.backend.document.base.Document;
import com.blazebit.persistence.view.EntityView;
import com.blazebit.persistence.view.EntityViewInheritance;
import com.blazebit.persistence.view.IdMapping;
import com.blazebit.persistence.view.Mapping;

import java.time.LocalDateTime;
import java.util.UUID;

@EntityView(Document.class)
@EntityViewInheritance
public interface DocumentWithStudentView {
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

    @Mapping("CONCAT(student.user.firstName, ' ', student.user.lastName)")
    String getStudentFullName();

    @Mapping("student.user.firstName")
    String getStudentFirstName();

    @Mapping("student.user.lastName")
    String getStudentLastName();

    @Mapping("student.program")
    String getStudentProgram();

    @Mapping("student.year")
    Short getStudentYear();

    @Mapping("student.user.picture")
    String getStudentPicture();

    LocalDateTime getUploadedAt();
}