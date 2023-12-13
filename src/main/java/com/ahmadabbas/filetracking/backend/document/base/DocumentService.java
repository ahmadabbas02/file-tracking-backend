package com.ahmadabbas.filetracking.backend.document.base;

import com.ahmadabbas.filetracking.backend.document.payload.DocumentResponse;
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


    public DocumentService(DocumentRepository documentRepository, DocumentDtoMapper documentDtoMapper) {
        this.documentRepository = documentRepository;
        this.documentDtoMapper = documentDtoMapper;
    }

    public DocumentResponse getAllDocuments(int pageNo, int pageSize, String sortBy, String order) {
        return getAllDocuments(pageNo, pageSize, sortBy, order, "-1");
    }

    public DocumentResponse getAllDocuments(int pageNo, int pageSize, String sortBy, String order, String studentId) {
        Sort sort = order.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        Page<Document> documentPage = studentId.equals("-1")
                ? documentRepository.findAll(pageable)
                : documentRepository.findByStudent_Id(studentId, pageable);
        List<DocumentDto> content = documentPage.getContent().stream().map(documentDtoMapper).toList();
        return new DocumentResponse(
                content,
                pageNo,
                pageSize,
                documentPage.getTotalElements(),
                documentPage.getTotalPages(),
                documentPage.isLast()
        );
    }


}
