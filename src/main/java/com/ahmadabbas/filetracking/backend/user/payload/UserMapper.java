package com.ahmadabbas.filetracking.backend.user.payload;

import com.ahmadabbas.filetracking.backend.advisor.Advisor;
import com.ahmadabbas.filetracking.backend.advisor.payload.AdvisorMapper;
import com.ahmadabbas.filetracking.backend.student.Student;
import com.ahmadabbas.filetracking.backend.student.payload.StudentMapper;
import com.ahmadabbas.filetracking.backend.user.Role;
import com.ahmadabbas.filetracking.backend.user.User;
import org.mapstruct.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    @Mapping(
            expression = "java(getRoles(userDto))",
            target = "roles"
    )
    @InheritInverseConfiguration(name = "toDto")
    User toEntity(UserDto userDto);

    @Mapping(
            expression = "java(getAdditionalInformation(user))",
            target = "roles"
    )
    UserDto toDto(User user);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    User partialUpdate(UserUpdateDto userUpdateDto, @MappingTarget User user);

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
        user.getRoles().forEach(role -> {
            if (!additionalInfo.containsKey(role.name())) {
                additionalInfo.put(role.name(), "");
            }
        });

        return additionalInfo;
    }

    default Set<Role> getRoles(UserDto userDto) {
        return userDto.roles().keySet().stream().map(k -> Role.valueOf(k.toUpperCase())).collect(Collectors.toSet());
    }
}