package com.ahmadabbas.filetracking.backend.advisor.payload;

import com.ahmadabbas.filetracking.backend.advisor.Advisor;
import com.ahmadabbas.filetracking.backend.document.base.payload.DocumentMapper;
import com.ahmadabbas.filetracking.backend.user.User;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface AdvisorMapper {
    AdvisorMapper INSTANCE = Mappers.getMapper(AdvisorMapper.class);


    @InheritInverseConfiguration(name = "toDto")
    Advisor toEntity(AdvisorDto advisorDto);

    @Mapping(expression = "java(getUserFullName(advisor.getUser()))", target = "name")
    AdvisorDto toDto(Advisor advisor);

    AdvisorUserDto toAdvisorUserDto(Advisor advisor);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Advisor partialUpdate(AdvisorDto advisorDto, @MappingTarget Advisor advisor);

    default String getUserFullName(User user) {
        return user.getName().getFullName();
    }
}