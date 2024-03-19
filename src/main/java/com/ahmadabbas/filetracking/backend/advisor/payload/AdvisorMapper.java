package com.ahmadabbas.filetracking.backend.advisor.payload;

import com.ahmadabbas.filetracking.backend.advisor.Advisor;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface AdvisorMapper {
    AdvisorMapper INSTANCE = Mappers.getMapper(AdvisorMapper.class);

    @InheritInverseConfiguration(name = "toDto")
    Advisor toEntity(AdvisorDto advisorDto);

    @Mapping(source = "user.fullName", target = "fullName")
    @Mapping(source = "user.firstName", target = "firstName")
    @Mapping(source = "user.lastName", target = "lastName")
    AdvisorDto toDto(Advisor advisor);

    AdvisorUserDto toAdvisorUserDto(Advisor advisor);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Advisor partialUpdate(AdvisorDto advisorDto, @MappingTarget Advisor advisor);

}