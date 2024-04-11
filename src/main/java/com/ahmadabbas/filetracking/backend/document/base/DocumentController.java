package com.ahmadabbas.filetracking.backend.document.base;

import com.ahmadabbas.filetracking.backend.document.base.payload.*;
import com.ahmadabbas.filetracking.backend.document.comment.Comment;
import com.ahmadabbas.filetracking.backend.document.comment.CommentService;
import com.ahmadabbas.filetracking.backend.document.comment.payload.CommentAddRequest;
import com.ahmadabbas.filetracking.backend.document.comment.payload.CommentDto;
import com.ahmadabbas.filetracking.backend.document.comment.payload.CommentMapper;
import com.ahmadabbas.filetracking.backend.document.contact.*;
import com.ahmadabbas.filetracking.backend.document.contact.payload.*;
import com.ahmadabbas.filetracking.backend.document.internship.*;
import com.ahmadabbas.filetracking.backend.document.internship.payload.*;
import com.ahmadabbas.filetracking.backend.document.medical.*;
import com.ahmadabbas.filetracking.backend.document.medical.payload.*;
import com.ahmadabbas.filetracking.backend.document.petition.*;
import com.ahmadabbas.filetracking.backend.document.petition.payload.*;
import com.ahmadabbas.filetracking.backend.user.User;
import com.ahmadabbas.filetracking.backend.util.payload.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/documents")
@Tag(name = "Document")
public class DocumentController {

    private final DocumentService documentService;
    private final ContactDocumentService contactDocumentService;
    private final InternshipDocumentService internshipDocumentService;
    private final PetitionDocumentService petitionDocumentService;
    private final CommentService commentService;
    private final CommentMapper commentMapper;
    private final MedicalReportDocumentService medicalReportDocumentService;

    @Operation(
            summary = "Get document information",
            description = """
                    Returns the fields related with the document.
                    """
    )
    @GetMapping("/{documentId}")
    public ResponseEntity<DocumentDto> getDocument(
            @AuthenticationPrincipal User loggedInUser,
            @PathVariable UUID documentId
    ) {
        Document document = documentService.getDocument(documentId, loggedInUser);
        return ResponseEntity.ok(document.toDto());
    }

    @Operation(
            summary = "Approve student petition",
            description = """
                    Returns the fields related with the approved document.
                    """
    )
    @PatchMapping("/{documentId}/approve")
    public ResponseEntity<PetitionDocumentDto> approveDocument(
            @AuthenticationPrincipal User loggedInUser,
            @PathVariable UUID documentId
    ) {
        PetitionDocument document = petitionDocumentService.approvePetitionDocument(documentId, loggedInUser);
        return ResponseEntity.ok(document.toDto());
    }

    @Operation(
            summary = "Delete document",
            description = """
                    Soft deletes a document
                    """
    )
    @DeleteMapping("/{documentId}/delete")
    public ResponseEntity<DocumentDto> deleteDocument(
            @AuthenticationPrincipal User user,
            @PathVariable UUID documentId
    ) {
        Document document = documentService.deleteDocument(documentId, user);
        return ResponseEntity.ok(document.toDto());
    }

    @Operation(
            summary = "Get document comments",
            description = """
                    Returns all the comments on a specific document.
                    """
    )
    @GetMapping("/{documentId}/comments")
    public ResponseEntity<List<CommentDto>> getAllComments(
            @AuthenticationPrincipal User user,
            @PathVariable UUID documentId
    ) {
        List<Comment> comments = commentService.getAllComments(documentId, user);
        return ResponseEntity.ok(comments.stream().map(commentMapper::toDto).toList());
    }

    @Operation(
            summary = "Add comment to document",
            description = """
                    Adds a comment to a specific document.
                    """
    )
    @PostMapping("/{documentId}/comments")
    public ResponseEntity<CommentDto> addComment(
            @AuthenticationPrincipal User user,
            @PathVariable UUID documentId,
            @Valid @RequestBody CommentAddRequest addRequest
    ) {
        Comment comment = commentService.addComment(addRequest, documentId, user);
        return ResponseEntity.ok(commentMapper.toDto(comment));
    }

    @Operation(
            summary = "Get all documents",
            description = """
                    Returns a pagination result of all documents in the database
                    sorted by default on `uploadedAt` and `descending` order.
                    """
    )
    @GetMapping("")
    public ResponseEntity<PaginatedResponse<DocumentDto>> getAllDocuments(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "1", required = false) int pageNo,
            @RequestParam(defaultValue = "10", required = false) int pageSize,
            @RequestParam(defaultValue = "uploadedAt", required = false) String sortBy,
            @RequestParam(defaultValue = "desc", required = false) String order,
            @RequestParam(defaultValue = "-1", required = false) String studentId,
            @RequestParam(defaultValue = "", required = false) String searchQuery,
            @RequestParam(defaultValue = "", required = false) List<Long> categoryIds
    ) {
        return ResponseEntity.ok(
                documentService.getAllDocuments(user, pageNo, pageSize, sortBy, order, searchQuery, studentId, categoryIds)
        );
    }

    @Operation(
            summary = "Get all document blobs",
            description = """
                    Returns a pagination result of all document blobs
                    sorted by default on `uploadedAt` and `descending` order.
                    """
    )
    @GetMapping("/blobs")
    public ResponseEntity<PaginatedMapResponse<String, byte[]>> getAllDocumentBlobs(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "1", required = false) int pageNo,
            @RequestParam(defaultValue = "10", required = false) int pageSize,
            @RequestParam(defaultValue = "uploadedAt", required = false) String sortBy,
            @RequestParam(defaultValue = "desc", required = false) String order,
            @RequestParam(defaultValue = "-1", required = false) String studentId,
            @RequestParam(defaultValue = "", required = false) List<Long> categoryIds
    ) throws IOException {
        return ResponseEntity.ok(
                documentService.getAllDocumentBlobs(user, pageNo, pageSize, sortBy, order, studentId, categoryIds)
        );
    }

    @Operation(
            summary = "Upload a document",
            description = """
                    Uploads a document to the cloud which can be
                    later previewed/downloaded using the UUID returned. \n
                    Example input for `data`: `{
                                           "title":"Test File",
                                           "description":"",
                                           "studentId":"23000002",
                                           "parentCategoryId":1,
                                           "categoryId":2
                                       }`
                    """
    )
    @PostMapping(value = "/upload", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<DocumentDto> upload(
            @RequestPart("file") MultipartFile file,
            @RequestPart("data") String data,
            @AuthenticationPrincipal User user
    ) throws IOException {
        DocumentAddRequest addRequest = new ObjectMapper().readValue(data, DocumentAddRequest.class);
        Document uploadedDocument = documentService.addDocument(file, addRequest, user);
        return new ResponseEntity<>(uploadedDocument.toDto(), HttpStatus.CREATED);
    }

    @Operation(
            summary = "Upload a document",
            description = """
                    Uploads a document to the cloud which can be
                    later previewed/downloaded using the UUID returned. \n
                    `type` can accept 'contact', 'petition', 'internship', 'medical' and 'normal' \n
                    Example input for `data`: `{
                                           "title":"Test File",
                                           "description":"",
                                           "studentId":"23000002",
                                           "parentCategoryId":1,
                                           "categoryId":2
                                       }`
                    """
    )
    @PostMapping(value = "/uploadd", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<DocumentDto> uploadTest(
            @RequestPart(value = "file", required = false) MultipartFile file,
            @RequestPart("data") String data,
            @RequestParam(defaultValue = "normal") String documentType,
            @AuthenticationPrincipal User loggedInUser
    ) throws IOException {
        switch (documentType) {
            case "contact":
                ContactDocumentAddRequest contactAddRequest = new ObjectMapper().readValue(data,
                        ContactDocumentAddRequest.class);
                ContactDocument contactDocument = contactDocumentService.addContactDocument(contactAddRequest,
                        loggedInUser);
                return new ResponseEntity<>(contactDocument.toDto(), HttpStatus.CREATED);
            case "petition":
                PetitionDocumentAddRequest petitionDocumentAddRequest = new ObjectMapper().readValue(data,
                        PetitionDocumentAddRequest.class);
                PetitionDocument petitionDocument =
                        petitionDocumentService.addPetitionDocument(petitionDocumentAddRequest, loggedInUser);
                return new ResponseEntity<>(petitionDocument.toDto(), HttpStatus.CREATED);
            case "internship":
                InternshipAddRequest internshipAddRequest = new ObjectMapper().readValue(data,
                        InternshipAddRequest.class);
                InternshipDocument internshipDocument = internshipDocumentService.addInternship(file,
                        internshipAddRequest, loggedInUser);
                return new ResponseEntity<>(internshipDocument.toDto(), HttpStatus.CREATED);
            case "medical":
                MedicalReportAddRequest medicalReportAddRequest = new ObjectMapper().readValue(data,
                        MedicalReportAddRequest.class);
                MedicalReportDocument medicalReportDocument = medicalReportDocumentService.addMedicalReport(file,
                        medicalReportAddRequest, loggedInUser);
                return new ResponseEntity<>(medicalReportDocument.toDto(), HttpStatus.CREATED);
            default:
                DocumentAddRequest normalAddRequest = new ObjectMapper().readValue(data, DocumentAddRequest.class);
                Document uploadedDocument = documentService.addDocument(file, normalAddRequest, loggedInUser);
                return new ResponseEntity<>(uploadedDocument.toDto(), HttpStatus.CREATED);
        }
    }

    @Operation(
            summary = "Upload contact document",
            description = """
                    Generates and uploads contact form document.
                    """
    )
    @PostMapping("/upload/contact")
    public ResponseEntity<ContactDocumentDto> postContactDocument(
            @Valid @RequestBody ContactDocumentAddRequest addRequest,
            @AuthenticationPrincipal User user
    ) {
        ContactDocument contactDocument = contactDocumentService.addContactDocument(addRequest, user);
        return new ResponseEntity<>(contactDocument.toDto(), HttpStatus.CREATED);
    }

    @Operation(
            summary = "Upload petition document",
            description = """
                    Generates and uploads petition document.
                    """
    )
    @PostMapping("/upload/petition")
    public ResponseEntity<PetitionDocumentDto> postPetitionDocument(
            @Valid @RequestBody PetitionDocumentAddRequest addRequest,
            @AuthenticationPrincipal User user
    ) {
        PetitionDocument petitionDocument = petitionDocumentService.addPetitionDocument(addRequest, user);
        return new ResponseEntity<>(petitionDocument.toDto(), HttpStatus.CREATED);
    }

    @Operation(
            summary = "Upload internship document",
            description = """
                    Uploads an internship document to the cloud which can be
                    later previewed/downloaded using the UUID returned. \n
                    Example input for `data`: `{
                                           "title":"Test File",
                                           "description":"",
                                           "studentId":"23000002",
                                           "numberOfWorkingDays":20
                                       }`
                    """
    )
    @PostMapping(value = "/upload/internship", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<InternshipDocumentDto> uploadInternshipDocument(
            @RequestPart("file") MultipartFile file,
            @RequestPart("data") String data,
            @AuthenticationPrincipal User loggedInUser
    ) throws IOException {
        InternshipAddRequest addRequest = new ObjectMapper().readValue(data, InternshipAddRequest.class);
        InternshipDocument internshipDocument = internshipDocumentService.addInternship(file, addRequest, loggedInUser);
        return new ResponseEntity<>(internshipDocument.toDto(), HttpStatus.CREATED);
    }

    @Operation(
            summary = "Upload medical document",
            description = """
                    Uploads an medical document to the cloud which can be
                    later previewed/downloaded using the UUID returned. \n
                    Example input for `data`: `{
                                        	"title": "Medical Report 1",
                                        	"description": "some text",
                                        	"dateOfAbsence": "2024-03-09"
                                        }`
                    """
    )
    @PostMapping(value = "/upload/medical-report", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<MedicalReportDto> uploadMedicalReportDocument(
            @RequestPart("file") MultipartFile file,
            @RequestPart("data") String data,
            @AuthenticationPrincipal User loggedInUser
    ) throws IOException {
        MedicalReportAddRequest addRequest = new ObjectMapper().readValue(data, MedicalReportAddRequest.class);
        MedicalReportDocument medicalReportDocument = medicalReportDocumentService.addMedicalReport(file, addRequest,
                loggedInUser);
        return new ResponseEntity<>(medicalReportDocument.toDto(), HttpStatus.CREATED);
    }

    @Operation(
            summary = "Modify document category",
            description = "Modifies a specific document category, mainly used to organize uploaded documents. "
    )
    @PatchMapping("/modify-category")
    public ResponseEntity<DocumentDto> modifyCategory(@Valid @RequestBody DocumentModifyCategoryRequest body,
                                                      @AuthenticationPrincipal User loggedInUser) {
        Document modifiedDocument = documentService.modifyDocumentCategory(body, loggedInUser);
        return ResponseEntity.ok(modifiedDocument.toDto());
    }

    @Operation(
            summary = "Get file preview",
            description = """
                    Display's a preview of the file linked to database with the specific `UUID`.
                    """
    )
    @GetMapping("/preview")
    public ResponseEntity<byte[]> getFilePreview(@AuthenticationPrincipal User user,
                                                 @RequestParam UUID uuid) throws IOException {
        byte[] data = documentService.getDocumentPreview(user, uuid);
        if (data != null) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentLength(data.length);
            headers.set("Content-Disposition", "inline; filename=" + uuid);
            headers.setContentType(MediaType.APPLICATION_PDF);
            return new ResponseEntity<>(data, headers, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Operation(
            summary = "Download multiple",
            description = """
                    Display's a preview of the file linked to database with the specific `UUID`.
                    """
    )
    @PostMapping("/download")
    public ResponseEntity<byte[]> downloadFiles(@AuthenticationPrincipal User user,
                                                @Valid @RequestBody DocumentDownloadRequest request) throws IOException {
        byte[] zipData = documentService.getDocumentsZip(user, request.uuids());
        if (zipData != null) {
            LocalDateTime now = LocalDateTime.now(ZoneId.of("Europe/Athens"));
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
            String timestamp = formatter.format(now);
            String downloadFilename = timestamp + ".zip";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentLength(zipData.length);
            headers.set("Content-Disposition", "attachment; filename=" + downloadFilename);
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            return new ResponseEntity<>(zipData, headers, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}
