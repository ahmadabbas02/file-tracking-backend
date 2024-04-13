package com.ahmadabbas.filetracking.backend.document.base;

import com.ahmadabbas.filetracking.backend.category.Category;
import com.ahmadabbas.filetracking.backend.category.CategoryService;
import com.ahmadabbas.filetracking.backend.document.base.payload.DocumentAddRequest;
import com.ahmadabbas.filetracking.backend.document.base.payload.DocumentApproveRequest;
import com.ahmadabbas.filetracking.backend.document.base.payload.DocumentModifyCategoryRequest;
import com.ahmadabbas.filetracking.backend.document.base.payload.DocumentPreview;
import com.ahmadabbas.filetracking.backend.document.base.repository.DocumentPreviewViewRepository;
import com.ahmadabbas.filetracking.backend.document.base.repository.DocumentRepository;
import com.ahmadabbas.filetracking.backend.document.base.repository.DocumentStudentIdViewRepository;
import com.ahmadabbas.filetracking.backend.document.base.repository.DocumentStudentViewRepository;
import com.ahmadabbas.filetracking.backend.document.base.view.DocumentPreviewView;
import com.ahmadabbas.filetracking.backend.document.base.view.DocumentStudentIdView;
import com.ahmadabbas.filetracking.backend.document.base.view.DocumentStudentView;
import com.ahmadabbas.filetracking.backend.document.medical.view.MedicalReportDocumentStudentView;
import com.ahmadabbas.filetracking.backend.document.petition.view.PetitionDocumentStudentView;
import com.ahmadabbas.filetracking.backend.exception.APIException;
import com.ahmadabbas.filetracking.backend.exception.ResourceNotFoundException;
import com.ahmadabbas.filetracking.backend.student.Student;
import com.ahmadabbas.filetracking.backend.student.StudentService;
import com.ahmadabbas.filetracking.backend.student.view.StudentAdvisorView;
import com.ahmadabbas.filetracking.backend.student.view.StudentView;
import com.ahmadabbas.filetracking.backend.user.Role;
import com.ahmadabbas.filetracking.backend.user.User;
import com.ahmadabbas.filetracking.backend.util.AzureBlobService;
import com.ahmadabbas.filetracking.backend.util.PagingUtils;
import com.ahmadabbas.filetracking.backend.util.payload.PaginatedMapResponse;
import com.ahmadabbas.filetracking.backend.util.payload.PaginatedResponse;
import com.blazebit.persistence.view.EntityViewManager;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Filter;
import org.hibernate.Session;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@Transactional(readOnly = true)
@Slf4j
@RequiredArgsConstructor
public class DocumentService {
    private final DocumentRepository documentRepository;
    private final CategoryService categoryService;
    private final StudentService studentService;
    private final AzureBlobService azureBlobService;
    private final EntityManager entityManager;
    private final EntityViewManager evm;
    private final DocumentStudentIdViewRepository documentStudentIdViewRepository;
    private final DocumentStudentViewRepository documentStudentViewRepository;
    private final DocumentPreviewViewRepository documentPreviewViewRepository;

    public DocumentStudentIdView getDocumentStudentIdView(UUID uuid, User loggedInUser) {
        Session session = entityManager.unwrap(Session.class);
        Filter filter = session.enableFilter("deletedDocumentFilter");
        filter.setParameter("isDeleted", false);
        DocumentStudentIdView document = documentStudentIdViewRepository.findById(uuid)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "document with id `%s` not found".formatted(uuid)
                ));
        session.disableFilter("deletedDocumentFilter");
        checkDocumentPermissions(loggedInUser,
                document.getStudentId(),
                document.getCategoryId(),
                document.getCategoryName());
        return document;
    }

    public DocumentStudentView getDocumentStudentView(UUID uuid, User loggedInUser) {
        Session session = entityManager.unwrap(Session.class);
        Filter filter = session.enableFilter("deletedDocumentFilter");
        filter.setParameter("isDeleted", false);
        DocumentStudentView document = documentStudentViewRepository.findById(uuid)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "document with id `%s` not found".formatted(uuid)
                ));
        session.disableFilter("deletedDocumentFilter");
        checkDocumentPermissions(loggedInUser,
                document.getStudentId(),
                document.getCategoryId(),
                document.getCategoryName());
        return document;
    }

    public DocumentPreviewView getDocumentPreviewView(UUID uuid, User loggedInUser) {
        Session session = entityManager.unwrap(Session.class);
        Filter filter = session.enableFilter("deletedDocumentFilter");
        filter.setParameter("isDeleted", false);
        DocumentPreviewView document = documentPreviewViewRepository.findById(uuid)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "document with id `%s` not found".formatted(uuid)
                ));
        session.disableFilter("deletedDocumentFilter");
        checkDocumentPermissions(loggedInUser,
                document.getStudentId(),
                document.getCategoryId(),
                document.getCategoryName());
        return document;
    }

    public Document getDocument(UUID uuid, User loggedInUser) {
        Session session = entityManager.unwrap(Session.class);
        Filter filter = session.enableFilter("deletedDocumentFilter");
        filter.setParameter("isDeleted", false);
        Document document = documentRepository.getDocumentById(uuid)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "document with id `%s` not found".formatted(uuid)
                ));
        session.disableFilter("deletedDocumentFilter");
        checkDocumentPermissions(loggedInUser,
                document.getStudent().getId(),
                document.getCategory().getCategoryId(),
                document.getCategory().getName());
        return document;
    }

    @Transactional
    public Document addDocument(MultipartFile file,
                                DocumentAddRequest addRequest,
                                User loggedInUser) throws IOException {
        if (loggedInUser.getRoles().contains(Role.STUDENT)) {
            throw new AccessDeniedException("not authorized..");
        }
        Category category = categoryService.getCategory(addRequest.categoryId(), loggedInUser);
        Student student = studentService.getStudent(addRequest.studentId(), loggedInUser);
        String cloudPath = azureBlobService.upload(file, addRequest.studentId(), category.getName(), addRequest.title());
        log.debug("cloudPath received from uploading file: %s".formatted(cloudPath));
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
        Category newCategory = categoryService.getCategory(request.categoryId(), loggedInUser);
        document.setCategory(newCategory);
        documentRepository.save(document);
        return document;
    }

    public DocumentPreview getDocumentPreview(User loggedInUser, UUID uuid) throws IOException {
        DocumentPreviewView document = getDocumentPreviewView(uuid, loggedInUser);
        try (InputStream inputStream = azureBlobService.getInputStream(document.getPath(), uuid)) {
            byte[] blob = inputStream.readAllBytes();
            return new DocumentPreview(document.getFileName(), blob);
        }
    }

    public byte[] getDocumentsZip(User loggedInUser, List<UUID> uuids) throws IOException {
        ByteArrayOutputStream zipOutputStream = new ByteArrayOutputStream();
        ZipOutputStream outputStream = new ZipOutputStream(zipOutputStream);

        for (var uuid : uuids) {
            Document document = getDocument(uuid, loggedInUser);
            InputStream blobInputStream = azureBlobService.getInputStream(document.getPath(), uuid);
            String originalFileName = document.getFileName();

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

    public PaginatedResponse<DocumentStudentView> getAllDocuments(User loggedInUser,
                                                                  int pageNo,
                                                                  int pageSize,
                                                                  String sortBy,
                                                                  String order,
                                                                  String searchQuery,
                                                                  List<Long> categoryIds) {
        return getAllDocuments(loggedInUser, pageNo, pageSize, sortBy, order, searchQuery, "-1", categoryIds);
    }

    public PaginatedResponse<DocumentStudentView> getAllDocuments(User loggedInUser,
                                                                  int pageNo,
                                                                  int pageSize,
                                                                  String sortBy,
                                                                  String order,
                                                                  String searchQuery,
                                                                  String studentId,
                                                                  List<Long> categoryIds) {
        log.debug("DocumentService.getAllDocuments");
        Session session = entityManager.unwrap(Session.class);
        Filter filter = session.enableFilter("deletedDocumentFilter");
        filter.setParameter("isDeleted", false);
        List<String> studentIds = Collections.emptyList();
        if (loggedInUser.getRoles().contains(Role.STUDENT)) {
            Student student = studentService.getStudentByUserId(loggedInUser.getId());
            studentId = student.getId();
            log.debug("setting studentId to the logged in student: {}", student.getId());
        } else if (loggedInUser.getRoles().contains(Role.ADVISOR)) {
            if (!studentId.equals("-1")) {
                StudentAdvisorView student = studentService.getStudentView(studentId, loggedInUser);
                if (!student.getAdvisor().getUserId().equals(loggedInUser.getId())) {
                    throw new AccessDeniedException("you are only allowed to get your own student's documents");
                }
            } else {
                studentIds = studentService.getAllStudentIds(loggedInUser);
                log.debug("setting studentIds to the advisor's students: {}", studentIds);
            }
        }
        List<Long> allowedCategoriesIds = categoryService.getAllowedCategoriesIds(loggedInUser.getRoles());
        boolean isMainCategoriesAllowed = categoryIds.isEmpty() || new HashSet<>(allowedCategoriesIds).containsAll(categoryIds);
        if (!isMainCategoriesAllowed) {
            log.debug("not allowed!");
            log.debug("categoryIds = {}", categoryIds);
            log.debug("allowedCategoriesIds = {}", allowedCategoriesIds);
            throw new AccessDeniedException("you are not allowed to get documents in given categories");
        }

        Pageable pageable = PagingUtils.getPageable(pageNo, pageSize, sortBy, order);
        Page<DocumentStudentView> documentPage;
        log.debug("loggedInUser = {}, pageNo = {}, pageSize = {}, sortBy = {}, order = {}, studentId = {}, " +
                  "categoryIds = {}",
                loggedInUser, pageNo, pageSize, sortBy, order, studentId, categoryIds);
        log.debug("allowedCategoriesIds = {}", allowedCategoriesIds);
        if (categoryIds.isEmpty()) {
            categoryIds = allowedCategoriesIds;
        }
        documentPage = documentRepository.findAllDocuments(studentId, studentIds, categoryIds, searchQuery, pageable);
        session.disableFilter("deletedDocumentFilter");
        List<DocumentStudentView> content = documentPage.getContent();
        return new PaginatedResponse<>(
                content,
                pageNo,
                pageSize,
                documentPage.getTotalElements(),
                documentPage.getTotalPages(),
                documentPage.isLast()
        );
    }

    public PaginatedMapResponse<String, byte[]> getAllDocumentBlobs(User loggedInUser,
                                                                    int pageNo,
                                                                    int pageSize,
                                                                    String sortBy,
                                                                    String order,
                                                                    String studentId,
                                                                    List<Long> categoryIds) throws IOException {
        log.debug("DocumentService.getAllDocumentBlobs");
        PaginatedResponse<DocumentStudentView> documents = getAllDocuments(loggedInUser, pageNo, pageSize, sortBy, order, studentId, categoryIds);

        Map<String, byte[]> blobs = new HashMap<>();

        for (var document : documents.results()) {
            String id = String.valueOf(document.getId());
            DocumentPreview preview = getDocumentPreview(loggedInUser, UUID.fromString(id));
            blobs.put(id, preview.blob());
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

    @Transactional
    public Document deleteDocument(UUID documentId, User loggedInUser) {
        Document document = getDocument(documentId, loggedInUser);
        documentRepository.delete(document);
        return document;
    }

    @Transactional
    public DocumentStudentView approveDocument(UUID documentId, User loggedInUser, DocumentApproveRequest approveRequest) {
        DocumentStatus.ApprovalStatus newStatus = approveRequest.approvalStatus();
        DocumentStudentView doc = getDocumentStudentView(documentId, loggedInUser);
        if (doc instanceof PetitionDocumentStudentView petitionDocument) {
            petitionDocument.setApprovalStatus(newStatus);
            evm.save(entityManager, petitionDocument);
            return petitionDocument;
        } else if (doc instanceof MedicalReportDocumentStudentView medicalReportDocument) {
            medicalReportDocument.setApprovalStatus(newStatus);
            evm.save(entityManager, medicalReportDocument);
            return medicalReportDocument;
        }
        throw new APIException(
                HttpStatus.BAD_REQUEST,
                "Only petition documents and medical report documents can be approved"
        );
    }

    private Set<Role> getAndCheckUserRolePermissions(User loggedInUser, Document document) {
        Set<Role> roles = loggedInUser.getRoles();
        if (roles.contains(Role.STUDENT)) {
            checkStudentDocumentPermissions(loggedInUser, document.getStudent().getId());
        } else if (roles.contains(Role.ADVISOR)) {
            checkAdvisorDocumentPermissions(loggedInUser);
        }
        return roles;
    }

    private void checkDocumentPermissions(User loggedInUser, String documentStudentId, Long categoryId, String categoryName) {
        Set<Role> roles = getAndCheckUserRolePermissions(loggedInUser, documentStudentId);
        List<Long> allowedCategories = categoryService.getAllowedCategoriesIds(roles);
        if (!allowedCategories.contains(categoryId)) {
            throw new AccessDeniedException(
                    "not authorized to view %s category".formatted(categoryName)
            );
        }
    }

    private Set<Role> getAndCheckUserRolePermissions(User loggedInUser, String documentStudentId) {
        Set<Role> roles = loggedInUser.getRoles();
        if (roles.contains(Role.STUDENT)) {
            checkStudentDocumentPermissions(loggedInUser, documentStudentId);
        } else if (roles.contains(Role.ADVISOR)) {
            checkAdvisorDocumentPermissions(loggedInUser);
        }
        return roles;
    }

    private void checkAdvisorDocumentPermissions(User loggedInUser) {
        StudentAdvisorView studentAdvisorView = studentService.getStudentAdvisorViewByUserId(loggedInUser.getId());
        if (!studentAdvisorView.getAdvisor().getUserId().equals(loggedInUser.getId())) {
            throw new AccessDeniedException("not authorized to get not own students documents.");
        }
    }

    private void checkStudentDocumentPermissions(User loggedInUser, String documentStudentId) {
        StudentView studentView = studentService.getStudentViewByUserId(loggedInUser.getId());
        if (!documentStudentId.equals(studentView.getId())) {
            throw new AccessDeniedException("not authorized to get other student's documents");
        }
    }

}
