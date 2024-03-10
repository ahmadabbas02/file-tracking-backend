package com.ahmadabbas.filetracking.backend.document.internship.payload;

import com.ahmadabbas.filetracking.backend.document.internship.InternshipDocument;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface InternshipDocumentMapper {
    InternshipDocumentMapper INSTANCE = Mappers.getMapper(InternshipDocumentMapper.class);

    @InheritInverseConfiguration(name = "toDto")
    InternshipDocument toEntity(InternshipDocumentDto internshipDto);

    @Mapping(source = "category.name", target = "categoryName")
    @Mapping(source = "category.categoryId", target = "categoryId")
    @Mapping(source = "category.parentCategoryId", target = "categoryParentId")
    @Mapping(source = "student.id", target = "studentId")
    @Mapping(source = "student.user.name", target = "studentName")
    @Mapping(source = "student.program", target = "studentProgram")
    @Mapping(source = "student.year", target = "studentYear")
    @Mapping(source = "student.user.picture", target = "studentPicture")
    InternshipDocumentDto toDto(InternshipDocument internshipDocument);

    @InheritConfiguration(name = "toEntity")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    InternshipDocument partialUpdate(InternshipDocumentDto internshipDto, @MappingTarget InternshipDocument internshipDocument);
}