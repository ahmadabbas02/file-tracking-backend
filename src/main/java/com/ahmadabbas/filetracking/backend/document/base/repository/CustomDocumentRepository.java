package com.ahmadabbas.filetracking.backend.document.base.repository;

import com.ahmadabbas.filetracking.backend.document.base.Document;
import com.ahmadabbas.filetracking.backend.document.base.view.DocumentPreviewView;
import com.ahmadabbas.filetracking.backend.document.base.view.DocumentStudentView;
import org.springframework.data.domain.*;

import java.util.*;


public interface CustomDocumentRepository {

    Optional<Document> getDocumentById(UUID id);

    Page<DocumentStudentView> findAllDocuments(String studentId,
                                               List<String> studentIds,
                                               List<Long> categoryIds,
                                               String searchQuery,
                                               Pageable pageable);

    List<DocumentPreviewView> findDocumentPreviews(List<UUID> uuids);
}
