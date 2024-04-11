package com.ahmadabbas.filetracking.backend.document.comment.payload;

import jakarta.validation.constraints.NotEmpty;

public record CommentAddRequest(
        @NotEmpty(message = "Comment message should not be empty") String message
) {
}
