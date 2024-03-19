package com.ahmadabbas.filetracking.backend.user.payload;

import com.ahmadabbas.filetracking.backend.advisor.Advisor;
import com.ahmadabbas.filetracking.backend.advisor.payload.AdvisorMapper;
import com.ahmadabbas.filetracking.backend.student.Student;
import com.ahmadabbas.filetracking.backend.student.payload.StudentMapper;
import com.ahmadabbas.filetracking.backend.user.Role;
import com.ahmadabbas.filetracking.backend.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import java.util.HashMap;
import java.util.Map;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {


    @Mapping(
            expression = "java(getAdditionalInformation(user))",
            target = "roles"
    )
    UserDto toDto(User user);

    default Map<String, Object> getAdditionalInformation(User user) {
        Map<String, Object> additionalInfo = new HashMap<>();

        Advisor advisor = user.getAdvisor();
        Student student = user.getStudent();
        if (advisor != null) {
            additionalInfo.put(Role.ADVISOR.name(), AdvisorMapper.INSTANCE.toAdvisorUserDto(advisor));
        }
        if (student != null) {
            additionalInfo.put(Role.STUDENT.name(), StudentMapper.INSTANCE.toStudentUserDto(student));
        }
        if (additionalInfo.isEmpty()) {
            user.getRoles().forEach(role -> additionalInfo.put(role.name(), ""));
        }

        return additionalInfo;
    }
}