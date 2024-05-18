package com.ahmadabbas.filetracking.backend.document.petition.payload;

import com.ahmadabbas.filetracking.backend.document.petition.PetitionDocument;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface PetitionDocumentMapper {
    PetitionDocumentMapper INSTANCE = Mappers.getMapper(PetitionDocumentMapper.class);

    @InheritInverseConfiguration(name = "toDto")
    PetitionDocument toEntity(PetitionDocumentDto petitionDocumentDto);

    @Mapping(source = "category.name", target = "categoryName")
    @Mapping(source = "category.categoryId", target = "categoryId")
    @Mapping(source = "category.parentCategoryId", target = "categoryParentId")
    @Mapping(source = "student.id", target = "studentId")
    @Mapping(source = "student.user.firstName", target = "studentFirstName")
    @Mapping(source = "student.user.lastName", target = "studentLastName")
    @Mapping(source = "student.user.fullName", target = "studentFullName")
    @Mapping(source = "student.program", target = "studentProgram")
    @Mapping(source = "student.year", target = "studentYear")
    @Mapping(source = "student.user.picture", target = "studentPicture")
    PetitionDocumentDto toDto(PetitionDocument petitionDocument);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    PetitionDocument partialUpdate(PetitionDocumentDto petitionDocumentDto, @MappingTarget PetitionDocument petitionDocument);

}