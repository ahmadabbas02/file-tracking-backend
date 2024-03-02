package com.ahmadabbas.filetracking.backend.document.base;

import com.ahmadabbas.filetracking.backend.category.Category;
import com.ahmadabbas.filetracking.backend.category.CategoryService;
import com.ahmadabbas.filetracking.backend.document.base.payload.DocumentAddRequest;
import com.ahmadabbas.filetracking.backend.document.base.payload.DocumentDto;
import com.ahmadabbas.filetracking.backend.document.base.payload.DocumentMapper;
import com.ahmadabbas.filetracking.backend.document.base.payload.DocumentModifyCategoryRequest;
import com.ahmadabbas.filetracking.backend.exception.ResourceNotFoundException;
import com.ahmadabbas.filetracking.backend.student.Student;
import com.ahmadabbas.filetracking.backend.student.StudentService;
import com.ahmadabbas.filetracking.backend.user.Role;
import com.ahmadabbas.filetracking.backend.user.User;
import com.ahmadabbas.filetracking.backend.util.AzureBlobService;
import com.ahmadabbas.filetracking.backend.util.PageableUtil;
import com.ahmadabbas.filetracking.backend.util.payload.PaginatedResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@Slf4j
@RequiredArgsConstructor
public class DocumentService {
    private final DocumentMapper documentMapper;

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
        Category category = categoryService.getCategory(addRequest.categoryId(), addRequest.parentCategoryId());
        Student student = studentService.getStudent(addRequest.studentId());
        String cloudPath = azureBlobService.upload(file, "/" + addRequest.studentId());
        log.info("cloudPath received from uploading file: %s".formatted(cloudPath));
        Document document = Document.builder()
                .category(category)
                .title(addRequest.title())
                .description(addRequest.description())
                .path(cloudPath)
                .student(student)
                .build();
//        student.addDocument(document);

        return documentRepository.save(document);
    }

    public Document modifyDocumentCategory(DocumentModifyCategoryRequest request) {
        Document document = getDocument(request.uuid());
        Category newCategory = categoryService.getCategory(request.categoryId(), request.parentCategoryId());
        document.setCategory(newCategory);
        return document;
    }

    public byte[] getDocumentPreview(Authentication authentication, UUID uuid) throws IOException {
        User loggedInUser = (User) authentication.getPrincipal();
        Document document = getDocument(uuid);
        if (loggedInUser.getRoles().contains(Role.STUDENT)
                && !Objects.equals(document.getStudent().getUser().getId(), loggedInUser.getId())) {
            throw new AccessDeniedException("not authorized");
        }
        try (InputStream inputStream = azureBlobService.getInputStream(document.getPath())) {
            return inputStream.readAllBytes();
        }
    }

    public PaginatedResponse<DocumentDto> getAllDocuments(
            Authentication authentication,
            int pageNo,
            int pageSize,
            String sortBy,
            String order
    ) {
        return getAllDocuments(authentication, pageNo, pageSize, sortBy, order, "-1");
    }

    public PaginatedResponse<DocumentDto> getAllDocuments(
            Authentication authentication,
            int pageNo,
            int pageSize,
            String sortBy,
            String order,
            String studentId
    ) {
        log.info("DocumentService.getAllDocuments");
        User loggedInUser = (User) authentication.getPrincipal();
        Student student;
        if (loggedInUser.getRoles().contains(Role.STUDENT)) {
            student = studentService.getStudentByUserId(loggedInUser.getId());
            studentId = student.getId();
            log.info("setting studentId to the logged in student: " + student.getId());
        }
        List<Long> allowedCategoriesIds = categoryService.getAllowedCategoriesIds(loggedInUser.getRoles());
        Pageable pageable = PageableUtil.getPageable(pageNo, pageSize, sortBy, order);
        Page<Document> documentPage = studentId.equals("-1")
                ? documentRepository.findAll(pageable)
                : documentRepository.findByStudentIdHavingCategoryIds(studentId, allowedCategoriesIds, pageable);
        List<DocumentDto> content = documentPage.getContent()
                .stream()
                .map(documentMapper::toDto)
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
