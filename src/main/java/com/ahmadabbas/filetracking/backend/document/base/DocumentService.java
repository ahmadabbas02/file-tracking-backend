package com.ahmadabbas.filetracking.backend.document.base;

import com.ahmadabbas.filetracking.backend.category.Category;
import com.ahmadabbas.filetracking.backend.category.CategoryService;
import com.ahmadabbas.filetracking.backend.document.base.payload.DocumentAddRequest;
import com.ahmadabbas.filetracking.backend.document.base.payload.DocumentDto;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;
import java.util.Set;
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

    public Document getDocument(UUID uuid, User loggedInUser) {
        Document document = documentRepository.findById(uuid)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "document with id `%s` not found".formatted(uuid)
                ));
        Set<Role> roles = loggedInUser.getRoles();
        if (roles.contains(Role.STUDENT)
                && !Objects.equals(document.getStudent().getUser().getId(), loggedInUser.getId())) {
            throw new AccessDeniedException("not authorized to get other student's documents");
        } else if (roles.contains(Role.ADVISOR)) {
            if (!document.getStudent().getAdvisor().getUser().getId().equals(loggedInUser.getId())) {
                throw new AccessDeniedException("not authorized to get not own student's documents.");
            }
        }
        List<Category> allowedCategories = categoryService.getAllowedCategories(roles);
        if (!allowedCategories.contains(document.getCategory())) {
            throw new AccessDeniedException("not authorized, not allowed to view %s category".formatted(document.getCategory().getName()));
        }
        return document;
    }

    @Transactional
    public Document uploadDocument(MultipartFile file,
                                   DocumentAddRequest addRequest,
                                   User loggedInUser) throws IOException {
        Category category = categoryService.getCategory(addRequest.categoryId(), addRequest.parentCategoryId(), loggedInUser);
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

        return documentRepository.save(document);
    }

    public Document modifyDocumentCategory(DocumentModifyCategoryRequest request, User loggedInUser) {
        Document document = getDocument(request.uuid(), loggedInUser);
        Category newCategory = categoryService.getCategory(request.categoryId(), request.parentCategoryId(), loggedInUser);
        document.setCategory(newCategory);
        return document;
    }

    public byte[] getDocumentPreview(User loggedInUser, UUID uuid) throws IOException {
        Document document = getDocument(uuid, loggedInUser);
        try (InputStream inputStream = azureBlobService.getInputStream(document.getPath())) {
            return inputStream.readAllBytes();
        }
    }

    public PaginatedResponse<DocumentDto> getAllDocuments(
            User loggedInUser,
            int pageNo,
            int pageSize,
            String sortBy,
            String order
    ) {
        return getAllDocuments(loggedInUser, pageNo, pageSize, sortBy, order, "-1");
    }

    public PaginatedResponse<DocumentDto> getAllDocuments(
            User loggedInUser,
            int pageNo,
            int pageSize,
            String sortBy,
            String order,
            Long categoryId,
            Long parentCategoryId
    ) {
        return getAllDocuments(loggedInUser, pageNo, pageSize, sortBy, order, "-1", categoryId, parentCategoryId);
    }

    public PaginatedResponse<DocumentDto> getAllDocuments(
            User loggedInUser,
            int pageNo,
            int pageSize,
            String sortBy,
            String order,
            String studentId
    ) {
        return getAllDocuments(loggedInUser, pageNo, pageSize, sortBy, order, studentId, -1L, -1L);
    }

    public PaginatedResponse<DocumentDto> getAllDocuments(
            User loggedInUser,
            int pageNo,
            int pageSize,
            String sortBy,
            String order,
            String studentId,
            Long categoryId,
            Long parentCategoryId
    ) {
        log.info("DocumentService.getAllDocuments");
        Student student;
        if (loggedInUser.getRoles().contains(Role.STUDENT)) {
            student = studentService.getStudentByUserId(loggedInUser.getId());
            studentId = student.getId();
            log.info("setting studentId to the logged in student: " + student.getId());
        }
        List<Long> allowedCategoriesIds = categoryService.getAllowedCategoriesIds(loggedInUser.getRoles());
        if (categoryId != -1L && parentCategoryId != -1L
                && !allowedCategoriesIds.contains(categoryId) && !allowedCategoriesIds.contains(parentCategoryId)) {
            throw new AccessDeniedException("you are not allowed to get documents");
        }
        Pageable pageable = PageableUtil.getPageable(pageNo, pageSize, sortBy, order);
        Page<Document> documentPage;
        if (studentId.equals("-1")) {
            if (categoryId == -1L && parentCategoryId == -1L) {
                documentPage = documentRepository.findAll(pageable);
            } else {
                documentPage = documentRepository.findByHavingCategoryIds(categoryId, parentCategoryId, pageable);
            }
        } else {
            if (categoryId == -1L && parentCategoryId == -1L) {
                documentPage = documentRepository.findByStudentIdHavingCategoryIds(studentId, allowedCategoriesIds, pageable);
            } else {
                documentPage = documentRepository.findByStudentIdHavingCategoryIds(studentId, categoryId, parentCategoryId, pageable);
            }
        }
        List<DocumentDto> content = documentPage.getContent()
                .stream()
                .map(Document::toDto)
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
