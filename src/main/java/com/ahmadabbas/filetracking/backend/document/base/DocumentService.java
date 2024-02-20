package com.ahmadabbas.filetracking.backend.document.base;

import com.ahmadabbas.filetracking.backend.category.Category;
import com.ahmadabbas.filetracking.backend.category.CategoryService;
import com.ahmadabbas.filetracking.backend.document.payload.DocumentAddRequest;
import com.ahmadabbas.filetracking.backend.document.payload.DocumentDto;
import com.ahmadabbas.filetracking.backend.document.payload.DocumentMapper;
import com.ahmadabbas.filetracking.backend.document.payload.DocumentModifyCategoryRequest;
import com.ahmadabbas.filetracking.backend.exception.ResourceNotFoundException;
import com.ahmadabbas.filetracking.backend.student.Student;
import com.ahmadabbas.filetracking.backend.student.StudentService;
import com.ahmadabbas.filetracking.backend.util.AzureBlobService;
import com.ahmadabbas.filetracking.backend.util.PageableUtil;
import com.ahmadabbas.filetracking.backend.util.payload.PaginatedResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@Slf4j
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final CategoryService categoryService;
    private final StudentService studentService;
    private final AzureBlobService azureBlobService;

    public Document getDocument(UUID uuid) {
        return documentRepository.findById(uuid)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "document with id `%s` not found".formatted(uuid)
                ));
    }

    @Transactional
    public Document uploadDocument(MultipartFile file, DocumentAddRequest addRequest) throws IOException {
        String url = azureBlobService.upload(file, "/" + addRequest.studentId());
        if (url.contains("failed")) {
            throw new RuntimeException("Failed to upload file!");
        }
        log.info("url received from uploading file: %s".formatted(url));
        Category category = categoryService.getCategory(addRequest.categoryId(), addRequest.parentCategoryId());
        Student student = studentService.getStudent(addRequest.studentId());
        Document document = Document.builder()
                .category(category)
                .title(addRequest.title())
                .description(addRequest.description())
                .path(url)
                .student(student)
                .build();
        return documentRepository.save(document);
    }

    public Document modifyDocumentCategory(DocumentModifyCategoryRequest request) {
        Document document = getDocument(request.uuid());
        Category newCategory = categoryService.getCategory(request.categoryId(), request.parentCategoryId());
        document.setCategory(newCategory);
        return documentRepository.save(document);
    }

    public byte[] getDocumentPreview(UUID uuid) throws IOException {
        Document document = getDocument(uuid);
        try (InputStream inputStream = azureBlobService.getInputStream(document.getPath())) {
            return inputStream.readAllBytes();
        }
    }

    public PaginatedResponse<DocumentDto> getAllDocuments(int pageNo, int pageSize, String sortBy, String order) {
        return getAllDocuments(pageNo, pageSize, sortBy, order, "-1");
    }

    public PaginatedResponse<DocumentDto> getAllDocuments(int pageNo, int pageSize, String sortBy, String order, String studentId) {
        Pageable pageable = PageableUtil.getPageable(pageNo, pageSize, sortBy, order);
        Page<Document> documentPage = studentId.equals("-1")
                ? documentRepository.findAll(pageable)
                : documentRepository.findByStudentId(studentId, pageable);
        List<DocumentDto> content = documentPage.getContent()
                .stream()
                .map(DocumentMapper.INSTANCE::toDto)
                .toList();
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
