package com.ahmadabbas.filetracking.backend.user.payload;

import com.ahmadabbas.filetracking.backend.advisor.AdvisorRepository;
import com.ahmadabbas.filetracking.backend.advisor.payload.AdvisorMapper;
import com.ahmadabbas.filetracking.backend.student.StudentRepository;
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


    @Mapping(source = "user.name.firstName", target = "firstName")
    @Mapping(source = "user.name.lastName", target = "lastName")
    @Mapping(expression = "java(getUserFullName(user))", target = "name")
    @Mapping(
            expression = "java(getAdditionalInformation(user, studentRepository, advisorRepository))",
            target = "roles"
    )
    UserDto toDto(User user, StudentRepository studentRepository, AdvisorRepository advisorRepository);

    default String getUserFullName(User user) {
        return user.getName().getFullName();
    }

    default Map<String, Object> getAdditionalInformation(User user,
                                                         StudentRepository studentRepository,
                                                         AdvisorRepository advisorRepository) {
        Map<String, Object> additionalInfo = new HashMap<>();
        if (user.getRoles().contains(Role.ADVISOR)) {
            advisorRepository.findByUserId(user.getId())
                    .ifPresent(
                            advisor -> additionalInfo.put(Role.ADVISOR.name(), AdvisorMapper.INSTANCE.toAdvisorUserDto(advisor))
                    );
        }
        if (user.getRoles().contains(Role.STUDENT)) {
            studentRepository.findByUserId(user.getId())
                    .ifPresent(
                            student -> additionalInfo.put(Role.STUDENT.name(), StudentMapper.INSTANCE.toStudentUserDto(student))
                    );
        }

        return additionalInfo;
    }
}