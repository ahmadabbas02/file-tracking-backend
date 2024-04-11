package com.ahmadabbas.filetracking.backend.document.comment.payload;

import com.ahmadabbas.filetracking.backend.document.comment.Comment;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface CommentMapper {

    @InheritInverseConfiguration(name = "toDto")
    Comment toEntity(CommentDto commentDto);

    @Mapping(source = "user.picture", target = "userPicture")
    @Mapping(source = "user.fullName", target = "userName")
    @Mapping(source = "user.firstName", target = "userFirstName")
    @Mapping(source = "user.lastName", target = "userLastName")
    @Mapping(source = "user.roles", target = "userRoles")
    CommentDto toDto(Comment comment);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Comment partialUpdate(CommentDto commentDto, @MappingTarget Comment comment);

}