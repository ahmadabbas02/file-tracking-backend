package com.ahmadabbas.filetracking.backend.document.medical.payload;

import com.ahmadabbas.filetracking.backend.document.medical.MedicalReportDocument;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface MedicalReportDocumentMapper {
    MedicalReportDocumentMapper INSTANCE = Mappers.getMapper(MedicalReportDocumentMapper.class);

    @InheritInverseConfiguration(name = "toDto")
    MedicalReportDocument toEntity(MedicalReportDto medicalReportDto);

    @Mapping(source = "category.name", target = "categoryName")
    @Mapping(source = "category.categoryId", target = "categoryId")
    @Mapping(source = "category.parentCategoryId", target = "categoryParentId")
    @Mapping(source = "student.id", target = "studentId")
    @Mapping(source = "student.user.name", target = "studentName")
    @Mapping(source = "student.department", target = "studentDepartment")
    @Mapping(source = "student.year", target = "studentYear")
    @Mapping(source = "student.user.picture", target = "studentPicture")
    MedicalReportDto toDto(MedicalReportDocument medicalReportDocument);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    MedicalReportDocument partialUpdate(MedicalReportDto medicalReportDto, @MappingTarget MedicalReportDocument medicalReportDocument);
}