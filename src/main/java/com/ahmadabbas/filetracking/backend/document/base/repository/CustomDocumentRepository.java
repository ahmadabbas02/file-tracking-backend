package com.ahmadabbas.filetracking.backend.document.base.repository;

import com.ahmadabbas.filetracking.backend.document.base.Document;
import org.springframework.data.domain.*;

import java.util.*;


public interface CustomDocumentRepository {

    Optional<Document> findOneById(UUID id);

    Page<Document> findAllDocuments(String studentId,
                                    List<String> studentIds,
                                    List<Long> categoryIds,
                                    String searchQuery,
                                    Pageable pageable);

}
