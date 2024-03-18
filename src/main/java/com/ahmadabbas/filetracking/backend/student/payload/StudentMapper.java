package com.ahmadabbas.filetracking.backend.student.payload;

import com.ahmadabbas.filetracking.backend.advisor.payload.AdvisorMapper;
import com.ahmadabbas.filetracking.backend.student.Student;
import com.ahmadabbas.filetracking.backend.user.User;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface StudentMapper {
    StudentMapper INSTANCE = Mappers.getMapper(StudentMapper.class);

    Student toEntity(StudentDto studentDto);

    @Mapping(expression = "java(getUserFullName(student.getUser()))", target = "name")
    @Mapping(expression = "java(getUserFullName(student.getAdvisor().getUser()))", target = "advisorName")
    @Mapping(source = "user.picture", target = "picture")
    @Mapping(source = "user.email", target = "email")
    @Mapping(source = "user.phoneNumber", target = "phoneNumber")
    StudentDto toDto(Student student);

    StudentUserDto toStudentUserDto(Student student);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Student partialUpdate(StudentDto studentDto, @MappingTarget Student student);

    default String getUserFullName(User user) {
        return user.getName().getFullName();
    }


}