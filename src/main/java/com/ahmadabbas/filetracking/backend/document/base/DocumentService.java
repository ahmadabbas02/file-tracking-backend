package com.ahmadabbas.filetracking.backend.document.base;

import com.ahmadabbas.filetracking.backend.category.*;
import com.ahmadabbas.filetracking.backend.document.base.payload.*;
import com.ahmadabbas.filetracking.backend.exception.ResourceNotFoundException;
import com.ahmadabbas.filetracking.backend.student.*;
import com.ahmadabbas.filetracking.backend.user.*;
import com.ahmadabbas.filetracking.backend.util.*;
import com.ahmadabbas.filetracking.backend.util.payload.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;
import java.util.zip.*;

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
        Set<Role> roles = getRoles(loggedInUser, document);
        List<Category> allowedCategories = categoryService.getAllowedCategories(roles);
        if (!allowedCategories.contains(document.getCategory())) {
            throw new AccessDeniedException("not authorized, not allowed to view %s category".formatted(document.getCategory().getName()));
        }
        return document;
    }

    @Transactional
    public Document addDocument(MultipartFile file,
                                DocumentAddRequest addRequest,
                                User loggedInUser) throws IOException {
        if (loggedInUser.getRoles().contains(Role.STUDENT)) {
            throw new AccessDeniedException("not authorized..");
        }
        Category category = categoryService.getCategory(addRequest.categoryId(), addRequest.parentCategoryId(), loggedInUser);
        Student student = studentService.getStudent(addRequest.studentId(), loggedInUser);
        String cloudPath = azureBlobService.upload(file, addRequest.studentId(), category.getName(), addRequest.title());
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
        try (InputStream inputStream = azureBlobService.getInputStream(document.getPath(), uuid)) {
            return inputStream.readAllBytes();
        }
    }

    public byte[] getDocumentsZip(User loggedInUser, List<UUID> uuids) throws IOException {
        ByteArrayOutputStream zipOutputStream = new ByteArrayOutputStream();
        ZipOutputStream outputStream = new ZipOutputStream(zipOutputStream);

        for (UUID uuid : uuids) {
            Document document = getDocument(uuid, loggedInUser);
            String path = document.getPath();

            InputStream blobInputStream = azureBlobService.getInputStream(path, uuid);
            int slashIndex = path.lastIndexOf("/");
            String originalFileName = path.substring(slashIndex + 1);

            outputStream.putNextEntry(new ZipEntry(originalFileName));
            byte[] buffer = new byte[1024];
            int length;
            while ((length = blobInputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            outputStream.closeEntry();
            blobInputStream.close();
        }
        outputStream.close();

        return zipOutputStream.toByteArray();
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
            List<Long> categoryIds,
            List<Long> parentCategoryIds
    ) {
        return getAllDocuments(loggedInUser, pageNo, pageSize, sortBy, order, "-1", categoryIds, parentCategoryIds);
    }

    public PaginatedResponse<DocumentDto> getAllDocuments(
            User loggedInUser,
            int pageNo,
            int pageSize,
            String sortBy,
            String order,
            String studentId
    ) {
        return getAllDocuments(loggedInUser, pageNo, pageSize, sortBy, order, studentId, Collections.emptyList(), Collections.emptyList());
    }

    public PaginatedResponse<DocumentDto> getAllDocuments(
            User loggedInUser,
            int pageNo,
            int pageSize,
            String sortBy,
            String order,
            String studentId,
            List<Long> categoryIds,
            List<Long> parentCategoryIds
    ) {
        log.info("DocumentService.getAllDocuments");
        List<String> studentIds = Collections.emptyList();
        if (loggedInUser.getRoles().contains(Role.STUDENT)) {
            Student student = studentService.getStudentByUserId(loggedInUser.getId());
            studentId = student.getId();
            log.info("setting studentId to the logged in student: {}", student.getId());
        } else if (loggedInUser.getRoles().contains(Role.ADVISOR)) {
            if (!studentId.equals("-1")) {
                Student student = studentService.getStudent(studentId, loggedInUser);
                if (!student.getAdvisor().getUser().getId().equals(loggedInUser.getId())) {
                    throw new AccessDeniedException("you are only allowed to get your own student's documents");
                }
            } else {
                studentIds = studentService.getAllStudentIds(loggedInUser);
            }
        }
        List<Long> allowedCategoriesIds = categoryService.getAllowedCategoriesIds(loggedInUser.getRoles());
        boolean isMainCategoriesAllowed = categoryIds.isEmpty() || new HashSet<>(allowedCategoriesIds).containsAll(categoryIds);
        boolean isChildrenCategoriesAllowed = parentCategoryIds.isEmpty() || new HashSet<>(allowedCategoriesIds).containsAll(parentCategoryIds);
        if (!isMainCategoriesAllowed || !isChildrenCategoriesAllowed) {
            log.info("categoryIds = {}", categoryIds);
            log.info("parentCategoryIds = {}", parentCategoryIds);
            log.info("allowedCategoriesIds = {}", allowedCategoriesIds);
            throw new AccessDeniedException("you are not allowed to get documents in given categories");
        }
        Pageable pageable = PageableUtil.getPageable(pageNo, pageSize, sortBy, order);
        Page<Document> documentPage;
        log.debug("loggedInUser = {}, pageNo = {}, pageSize = {}, sortBy = {}, order = {}, studentId = {}, categoryIds = {}, parentCategoryIds = {}", loggedInUser, pageNo, pageSize, sortBy, order, studentId, categoryIds, parentCategoryIds);
        log.debug("allowedCategoriesIds = {}", allowedCategoriesIds);
        if (studentId.equals("-1")) {
            if (categoryIds.isEmpty() && parentCategoryIds.isEmpty()) {
                if (studentIds.isEmpty()) {
                    documentPage = documentRepository.findAll(pageable);
                } else {
                    documentPage = documentRepository.findAll(pageable, studentIds);
                }
            } else {
                if (parentCategoryIds.isEmpty()) {
                    parentCategoryIds.add(-1L);
                }
                if (studentIds.isEmpty()) {
                    documentPage = documentRepository.findByHavingCategoryIds(categoryIds, parentCategoryIds, pageable);
                } else {
                    documentPage = documentRepository.findByHavingCategoryIds(categoryIds, parentCategoryIds, pageable, studentIds);
                }
            }
        } else {
            if (categoryIds.isEmpty() && parentCategoryIds.isEmpty()) {
                documentPage = documentRepository.findByStudentIdHavingCategoryIds(studentId, allowedCategoriesIds, pageable);
            } else {
                if (parentCategoryIds.isEmpty()) {
                    parentCategoryIds.add(-1L);
                }
                documentPage = documentRepository.findByStudentIdHavingCategoryIds(studentId, categoryIds, parentCategoryIds, pageable);
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

    public PaginatedMapResponse<String, byte[]> getAllDocumentBlobs(
            User loggedInUser,
            int pageNo,
            int pageSize,
            String sortBy,
            String order,
            List<Long> categoryIds,
            List<Long> parentCategoryIds
    ) throws IOException {
        return getAllDocumentBlobs(loggedInUser, pageNo, pageSize, sortBy, order, "-1", categoryIds, parentCategoryIds);
    }

    public PaginatedMapResponse<String, byte[]> getAllDocumentBlobs(
            User loggedInUser,
            int pageNo,
            int pageSize,
            String sortBy,
            String order,
            String studentId,
            List<Long> categoryIds,
            List<Long> parentCategoryIds
    ) throws IOException {
        log.info("DocumentService.getAllDocumentBlobs");
        PaginatedResponse<DocumentDto> documents = getAllDocuments(loggedInUser, pageNo, pageSize, sortBy, order, studentId, categoryIds, parentCategoryIds);


        Map<String, byte[]> blobs = new HashMap<>();

        for (var document : documents.results()) {
            var id = document.getId();
            var blob = getDocumentPreview(loggedInUser, UUID.fromString(id));
            blobs.put(id, blob);
        }

        return new PaginatedMapResponse<>(
                blobs,
                pageNo,
                pageSize,
                documents.totalElements(),
                documents.totalPages(),
                documents.isLastPage()
        );
    }

    private Set<Role> getRoles(User loggedInUser, Document document) {
        Set<Role> roles = loggedInUser.getRoles();
        if (roles.contains(Role.STUDENT)
                && !Objects.equals(document.getStudent().getUser().getId(), loggedInUser.getId())) {
            throw new AccessDeniedException("not authorized to get other student's documents");
        } else if (roles.contains(Role.ADVISOR)) {
            if (!document.getStudent().getAdvisor().getUser().getId().equals(loggedInUser.getId())) {
                throw new AccessDeniedException("not authorized to get not own student's documents.");
            }
        }
        return roles;
    }
}
