package com.ahmadabbas.filetracking.backend.document.contact.payload;

import com.ahmadabbas.filetracking.backend.document.contact.ContactDocument;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface ContactDocumentMapper {

    @InheritInverseConfiguration(name = "toDto")
    ContactDocument toEntity(ContactDocumentDto contactDocumentDto);

    @Mapping(source = "student.user.name", target = "studentName")
    @Mapping(source = "student.user.picture", target = "studentPicture")
    @Mapping(source = "student.year", target = "studentYear")
    @Mapping(source = "student.department", target = "studentDepartment")
    @Mapping(source = "student.id", target = "studentId")
    @Mapping(source = "category.name", target = "categoryName")
    @Mapping(source = "category.categoryId", target = "categoryCategoryId")
    @Mapping(source = "category.parentCategoryId", target = "categoryParentCategoryId")
    ContactDocumentDto toDto(ContactDocument contactDocument);

    @InheritConfiguration(name = "toEntity")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    ContactDocument partialUpdate(ContactDocumentDto contactDocumentDto, @MappingTarget ContactDocument contactDocument);
}