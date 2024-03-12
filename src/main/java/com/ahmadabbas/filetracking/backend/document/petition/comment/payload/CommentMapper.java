package com.ahmadabbas.filetracking.backend.document.petition.comment.payload;

import com.ahmadabbas.filetracking.backend.document.petition.comment.Comment;
import com.ahmadabbas.filetracking.backend.user.User;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface CommentMapper {

    @InheritInverseConfiguration(name = "toDto")
    Comment toEntity(CommentDto commentDto);

    @Mapping(source = "user.picture", target = "userPicture")
    @Mapping(expression = "java(getUserFullName(comment.getUser()))", target = "userName")
    @Mapping(source = "user.roles", target = "userRoles")
    CommentDto toDto(Comment comment);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Comment partialUpdate(CommentDto commentDto, @MappingTarget Comment comment);

    default String getUserFullName(User user) {
        return user.getName().getFullName();
    }
}