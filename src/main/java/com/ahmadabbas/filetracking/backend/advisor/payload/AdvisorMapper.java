package com.ahmadabbas.filetracking.backend.advisor.payload;

import com.ahmadabbas.filetracking.backend.advisor.Advisor;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface AdvisorMapper {
    @Mapping(source = "name", target = "user.name")
    Advisor toEntity(AdvisorDto advisorDto);

    @Mapping(source = "user.name", target = "name")
    AdvisorDto toDto(Advisor advisor);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(source = "name", target = "user.name")
    Advisor partialUpdate(AdvisorDto advisorDto, @MappingTarget Advisor advisor);
}