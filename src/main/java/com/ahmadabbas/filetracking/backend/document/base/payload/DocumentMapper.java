package com.ahmadabbas.filetracking.backend.document.base.payload;

import com.ahmadabbas.filetracking.backend.document.base.Document;
import com.ahmadabbas.filetracking.backend.user.User;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface DocumentMapper {
    DocumentMapper INSTANCE = Mappers.getMapper(DocumentMapper.class);

    @InheritInverseConfiguration(name = "toDto")
    Document toEntity(DocumentDto documentDto);

    @Deprecated
    @Mapping(expression = "java(getStudentFullName(document))", target = "studentName")
    @Mapping(source = "student.user.picture", target = "studentPicture")
    @Mapping(source = "student.year", target = "studentYear")
    @Mapping(source = "student.program", target = "studentProgram")
    @Mapping(source = "student.id", target = "studentId")
    @Mapping(source = "category.name", target = "categoryName")
    @Mapping(source = "category.categoryId", target = "categoryId")
    @Mapping(source = "category.parentCategoryId", target = "categoryParentId")
    DocumentDto toDto(Document document);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Document partialUpdate(DocumentDto documentDto1, @MappingTarget Document document);

    default String getStudentFullName(Document document) {
        User user = document.getStudent().getUser();
        return user.getName().getFullName();
    }
}