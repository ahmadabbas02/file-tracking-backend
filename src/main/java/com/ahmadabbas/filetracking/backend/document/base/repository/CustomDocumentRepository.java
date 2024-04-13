package com.ahmadabbas.filetracking.backend.document.base.repository;

import com.ahmadabbas.filetracking.backend.document.base.Document;
import com.ahmadabbas.filetracking.backend.document.base.views.DocumentWithStudentView;
import com.ahmadabbas.filetracking.backend.document.base.views.DocumentWithStudentIdView;
import org.springframework.data.domain.*;

import java.util.*;


public interface CustomDocumentRepository {

    Optional<Document> getDocumentById(UUID id);

    Optional<DocumentWithStudentView> getDocumentWithStudentViewById(UUID id);

    Optional<DocumentWithStudentIdView> getDocumentWithStudentIdView(UUID uuid);

    Page<DocumentWithStudentView> findAllDocuments(String studentId,
                                                   List<String> studentIds,
                                                   List<Long> categoryIds,
                                                   String searchQuery,
                                                   Pageable pageable);

}
