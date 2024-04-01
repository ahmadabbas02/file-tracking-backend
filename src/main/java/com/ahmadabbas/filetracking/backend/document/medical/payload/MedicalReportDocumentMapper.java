package com.ahmadabbas.filetracking.backend.document.medical.payload;

import com.ahmadabbas.filetracking.backend.document.medical.MedicalReportDocument;
import com.ahmadabbas.filetracking.backend.user.User;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface MedicalReportDocumentMapper {
    MedicalReportDocumentMapper INSTANCE = Mappers.getMapper(MedicalReportDocumentMapper.class);

    @InheritInverseConfiguration(name = "toDto")
    MedicalReportDocument toEntity(MedicalReportDto medicalReportDto);

    @Deprecated
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
    MedicalReportDto toDto(MedicalReportDocument medicalReportDocument);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    MedicalReportDocument partialUpdate(MedicalReportDto medicalReportDto, @MappingTarget MedicalReportDocument medicalReportDocument);

}