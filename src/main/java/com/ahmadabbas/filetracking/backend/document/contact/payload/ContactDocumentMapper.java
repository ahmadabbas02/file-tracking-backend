package com.ahmadabbas.filetracking.backend.document.contact.payload;

import com.ahmadabbas.filetracking.backend.document.contact.ContactDocument;
import com.ahmadabbas.filetracking.backend.user.User;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface ContactDocumentMapper {
    ContactDocumentMapper INSTANCE = Mappers.getMapper(ContactDocumentMapper.class);

    @InheritInverseConfiguration(name = "toDto")
    ContactDocument toEntity(ContactDocumentDto contactDocumentDto);

    @Deprecated
    @Mapping(source = "student.user.firstName", target = "studentFirstName")
    @Mapping(source = "student.user.lastName", target = "studentLastName")
    @Mapping(source = "student.user.fullName", target = "studentFullName")
    @Mapping(source = "student.user.picture", target = "studentPicture")
    @Mapping(source = "student.year", target = "studentYear")
    @Mapping(source = "student.program", target = "studentProgram")
    @Mapping(source = "student.id", target = "studentId")
    @Mapping(source = "category.name", target = "categoryName")
    @Mapping(source = "category.categoryId", target = "categoryId")
    @Mapping(source = "category.parentCategoryId", target = "categoryParentId")
    ContactDocumentDto toDto(ContactDocument contactDocument);

    @InheritConfiguration(name = "toEntity")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    ContactDocument partialUpdate(ContactDocumentDto contactDocumentDto, @MappingTarget ContactDocument contactDocument);

}