package com.ahmadabbas.filetracking.backend.document.comment.payload;

import com.ahmadabbas.filetracking.backend.user.Role;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

public record CommentDto(
        Long id,
        String message,
        String userName,
        String userFirstName,
        String userLastName,
        String userPicture,
        Set<Role> userRoles,
        LocalDateTime postedAt
) implements Serializable {
}