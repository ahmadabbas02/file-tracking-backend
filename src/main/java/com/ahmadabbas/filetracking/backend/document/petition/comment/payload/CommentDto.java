package com.ahmadabbas.filetracking.backend.document.petition.comment.payload;

import com.ahmadabbas.filetracking.backend.user.Role;
import lombok.Value;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * DTO for {@link com.ahmadabbas.filetracking.backend.document.petition.comment.Comment}
 */
@Value
public class CommentDto implements Serializable {
    Long id;
    String message;
    String userName;
    String userPicture;
    Set<Role> userRoles;
    LocalDateTime postedAt;
}