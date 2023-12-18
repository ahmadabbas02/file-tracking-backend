package com.ahmadabbas.filetracking.backend.document.base;

import com.ahmadabbas.filetracking.backend.util.payload.PaginatedResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/documents")
public class DocumentController {
    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @GetMapping
    public ResponseEntity<PaginatedResponse<DocumentDto>> getAllDocuments(
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "uploadedAt", required = false) String sortBy,
            @RequestParam(value = "order", defaultValue = "desc", required = false) String order
    ) {
        return ResponseEntity.ok(documentService.getAllDocuments(pageNo, pageSize, sortBy, order));
    }

    @GetMapping("{studentId}")
    public ResponseEntity<PaginatedResponse<DocumentDto>> getAllDocuments(
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "uploadedAt", required = false) String sortBy,
            @RequestParam(value = "order", defaultValue = "desc", required = false) String order,
            @PathVariable String studentId
    ) {
        return ResponseEntity.ok(documentService.getAllDocuments(pageNo, pageSize, sortBy, order, studentId));
    }
}
