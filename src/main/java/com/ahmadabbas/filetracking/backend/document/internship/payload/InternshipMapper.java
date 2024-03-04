package com.ahmadabbas.filetracking.backend.document.internship.payload;

import com.ahmadabbas.filetracking.backend.document.internship.InternshipDocument;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface InternshipMapper {

    @InheritInverseConfiguration(name = "toDto")
    InternshipDocument toEntity(InternshipDto internshipDto);

    @Mapping(source = "category.name", target = "categoryName")
    @Mapping(source = "category.categoryId", target = "categoryId")
    @Mapping(source = "category.parentCategoryId", target = "parentCategoryId")
    InternshipDto toDto(InternshipDocument internshipDocument);

    @InheritConfiguration(name = "toEntity")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    InternshipDocument partialUpdate(InternshipDto internshipDto, @MappingTarget InternshipDocument internshipDocument);
}