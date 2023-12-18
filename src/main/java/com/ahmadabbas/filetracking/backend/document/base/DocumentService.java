package com.ahmadabbas.filetracking.backend.document.base;

import com.ahmadabbas.filetracking.backend.util.PageableUtil;
import com.ahmadabbas.filetracking.backend.util.payload.PaginatedResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final DocumentDtoMapper documentDtoMapper;

    private final PageableUtil pageableUtil;


    public DocumentService(DocumentRepository documentRepository, DocumentDtoMapper documentDtoMapper, PageableUtil pageableUtil) {
        this.documentRepository = documentRepository;
        this.documentDtoMapper = documentDtoMapper;
        this.pageableUtil = pageableUtil;
    }

    public PaginatedResponse<DocumentDto> getAllDocuments(int pageNo, int pageSize, String sortBy, String order) {
        return getAllDocuments(pageNo, pageSize, sortBy, order, "-1");
    }

    public PaginatedResponse<DocumentDto> getAllDocuments(int pageNo, int pageSize, String sortBy, String order, String studentId) {
        Pageable pageable = pageableUtil.getPageable(pageNo, pageSize, sortBy, order);

        Page<Document> documentPage = studentId.equals("-1")
                ? documentRepository.findAll(pageable)
                : documentRepository.findByStudent_Id(studentId, pageable);
        List<DocumentDto> content = documentPage.getContent().stream().map(documentDtoMapper).toList();
        return new PaginatedResponse<>(
                content,
                pageNo,
                pageSize,
                documentPage.getTotalElements(),
                documentPage.getTotalPages(),
                documentPage.isLast()
        );
    }


}
