package com.ahmadabbas.filetracking.backend.student.payload;

import com.ahmadabbas.filetracking.backend.student.Student;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface StudentMapper {
    StudentMapper INSTANCE = Mappers.getMapper(StudentMapper.class);

    @InheritInverseConfiguration(name = "toDto")
    Student toEntity(StudentDto studentDto);

    @Mapping(source = "user.picture", target = "picture")
    @Mapping(source = "user.email", target = "email")
    @Mapping(source = "user.phoneNumber", target = "phoneNumber")
    @Mapping(source = "user.firstName", target = "firstName")
    @Mapping(source = "user.lastName", target = "lastName")
    @Mapping(source = "user.fullName", target = "fullName")
    @Mapping(source = "advisor.user.fullName", target = "advisorName")
    StudentDto toDto(Student student);

    StudentUserDto toStudentUserDto(Student student);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Student partialUpdate(StudentUpdateDto updateDto, @MappingTarget Student student);

}