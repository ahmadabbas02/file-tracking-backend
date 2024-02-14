package com.ahmadabbas.filetracking.backend.advisor.payload;

import com.ahmadabbas.filetracking.backend.advisor.Advisor;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface AdvisorMapper {

    AdvisorMapper INSTANCE = Mappers.getMapper(AdvisorMapper.class);

    @Mapping(source = "name", target = "user.name")
    Advisor toEntity(AdvisorDto advisorDto);

    @Mapping(source = "user.name", target = "name")
    AdvisorDto toDto(Advisor advisor);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(source = "name", target = "user.name")
    Advisor partialUpdate(AdvisorDto advisorDto, @MappingTarget Advisor advisor);
}