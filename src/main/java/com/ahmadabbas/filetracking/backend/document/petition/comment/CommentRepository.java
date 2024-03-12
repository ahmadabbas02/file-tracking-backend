package com.ahmadabbas.filetracking.backend.document.petition.comment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query("""
            select c from Comment c
            join fetch Document d
            on d.id = c.document.id
            join fetch User u
            on u.id = c.user.id
            where c.document.id = :id
            order by c.postedAt desc
            """)
    List<Comment> findAllByDocumentId(UUID id);

}