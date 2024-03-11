package com.ahmadabbas.filetracking.backend.document.petition.comment;

import com.ahmadabbas.filetracking.backend.document.base.Document;
import com.ahmadabbas.filetracking.backend.document.base.DocumentService;
import com.ahmadabbas.filetracking.backend.document.petition.comment.payload.CommentAddRequest;
import com.ahmadabbas.filetracking.backend.user.Role;
import com.ahmadabbas.filetracking.backend.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class CommentService {
    private final CommentRepository commentRepository;
    private final DocumentService documentService;

    public List<Comment> getAllComments(UUID documentId, User loggedInUser) {
        Document doc = documentService.getDocument(documentId, loggedInUser);
        return commentRepository.findAllByDocumentId(doc.getId());
    }

    @Transactional
    public Comment addComment(CommentAddRequest addRequest, UUID documentId, User loggedInUser) {
        Set<Role> roles = loggedInUser.getRoles();
        if (roles.contains(Role.STUDENT)) {
            throw new AccessDeniedException("not authorized to add comments");
        }
        Document doc = documentService.getDocument(documentId, loggedInUser);
        String categoryName = doc.getCategory().getName().toLowerCase();
        if (!categoryName.contains("petition")
                && !categoryName.contains("medical report")) {
            throw new AccessDeniedException("comments can only be added to petition and medical report!");
        }
        Comment comment = Comment.builder()
                .document(doc)
                .user(loggedInUser)
                .message(addRequest.message())
                .build();
        return commentRepository.save(comment);
    }
}
