package com.ahmadabbas.filetracking.backend.document.base;

import com.ahmadabbas.filetracking.backend.document.base.payload.*;
import com.ahmadabbas.filetracking.backend.document.base.view.DocumentStudentView;
import com.ahmadabbas.filetracking.backend.document.comment.Comment;
import com.ahmadabbas.filetracking.backend.document.comment.CommentService;
import com.ahmadabbas.filetracking.backend.document.comment.payload.CommentAddRequest;
import com.ahmadabbas.filetracking.backend.document.comment.payload.CommentDto;
import com.ahmadabbas.filetracking.backend.document.comment.payload.CommentMapper;
import com.ahmadabbas.filetracking.backend.document.contact.ContactDocument;
import com.ahmadabbas.filetracking.backend.document.contact.ContactDocumentService;
import com.ahmadabbas.filetracking.backend.document.contact.payload.ContactDocumentAddRequest;
import com.ahmadabbas.filetracking.backend.document.contact.payload.ContactDocumentDto;
import com.ahmadabbas.filetracking.backend.document.internship.InternshipDocument;
import com.ahmadabbas.filetracking.backend.document.internship.InternshipDocumentService;
import com.ahmadabbas.filetracking.backend.document.internship.payload.InternshipAddRequest;
import com.ahmadabbas.filetracking.backend.document.internship.payload.InternshipDocumentDto;
import com.ahmadabbas.filetracking.backend.document.medical.MedicalReportDocument;
import com.ahmadabbas.filetracking.backend.document.medical.MedicalReportDocumentService;
import com.ahmadabbas.filetracking.backend.document.medical.payload.MedicalReportAddRequest;
import com.ahmadabbas.filetracking.backend.document.medical.payload.MedicalReportDto;
import com.ahmadabbas.filetracking.backend.document.petition.PetitionDocument;
import com.ahmadabbas.filetracking.backend.document.petition.PetitionDocumentService;
import com.ahmadabbas.filetracking.backend.document.petition.payload.PetitionDocumentAddRequest;
import com.ahmadabbas.filetracking.backend.document.petition.payload.PetitionDocumentDto;
import com.ahmadabbas.filetracking.backend.user.UserPrincipal;
import com.ahmadabbas.filetracking.backend.util.payload.PaginatedMapResponse;
import com.ahmadabbas.filetracking.backend.util.payload.PaginatedResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

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
    public ResponseEntity<DocumentStudentView> getDocument(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable UUID documentId
    ) {
        DocumentStudentView document = documentService.getDocumentStudentView(documentId, principal.getUserEntity());
        return ResponseEntity.ok(document);
    }

    @Operation(
            summary = "Approve student petition",
            description = """
                    Returns the fields related with the approved document.
                    """
    )
    @PatchMapping("/{documentId}/approve")
    public ResponseEntity<DocumentStudentView> approveDocument(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable UUID documentId,
            @RequestBody @Valid DocumentApproveRequest approveRequest
    ) {
        DocumentStudentView document = documentService.approveDocument(documentId, principal.getUserEntity(), approveRequest);
        return ResponseEntity.ok(document);
    }

    @Operation(
            summary = "Delete document",
            description = """
                    Soft deletes a document
                    """
    )
    @DeleteMapping("/{documentId}/delete")
    public ResponseEntity<DocumentDto> deleteDocument(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable UUID documentId
    ) {
        Document document = documentService.deleteDocument(documentId, principal.getUserEntity());
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
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable UUID documentId
    ) {
        List<Comment> comments = commentService.getAllComments(documentId, principal.getUserEntity());
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
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable UUID documentId,
            @Valid @RequestBody CommentAddRequest addRequest
    ) {
        Comment comment = commentService.addComment(addRequest, documentId, principal.getUserEntity());
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
    public ResponseEntity<PaginatedResponse<DocumentStudentView>> getAllDocuments(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(defaultValue = "1", required = false) int pageNo,
            @RequestParam(defaultValue = "10", required = false) int pageSize,
            @RequestParam(defaultValue = "uploadedAt", required = false) String sortBy,
            @RequestParam(defaultValue = "desc", required = false) String order,
            @RequestParam(defaultValue = "-1", required = false) String studentId,
            @RequestParam(defaultValue = "", required = false) String searchQuery,
            @RequestParam(defaultValue = "", required = false) List<Long> categoryIds
    ) {
        return ResponseEntity.ok(
                documentService.getAllDocuments(principal.getUserEntity(), pageNo, pageSize, sortBy, order, searchQuery, studentId, categoryIds)
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
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(defaultValue = "1", required = false) int pageNo,
            @RequestParam(defaultValue = "10", required = false) int pageSize,
            @RequestParam(defaultValue = "uploadedAt", required = false) String sortBy,
            @RequestParam(defaultValue = "desc", required = false) String order,
            @RequestParam(defaultValue = "-1", required = false) String studentId,
            @RequestParam(defaultValue = "", required = false) List<Long> categoryIds
    ) throws IOException {
        return ResponseEntity.ok(
                documentService.getAllDocumentBlobs(principal.getUserEntity(), pageNo, pageSize, sortBy, order, studentId, categoryIds)
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
            @AuthenticationPrincipal UserPrincipal principal
    ) throws IOException {
        DocumentAddRequest addRequest = new ObjectMapper().readValue(data, DocumentAddRequest.class);
        Document uploadedDocument = documentService.addDocument(file, addRequest, principal.getUserEntity());
        return new ResponseEntity<>(uploadedDocument.toDto(), HttpStatus.CREATED);
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
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        ContactDocument contactDocument = contactDocumentService.addContactDocument(addRequest, principal.getUserEntity());
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
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        PetitionDocument petitionDocument = petitionDocumentService.addPetitionDocument(addRequest, principal.getUserEntity());
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
            @AuthenticationPrincipal UserPrincipal principal
    ) throws IOException {
        InternshipAddRequest addRequest = new ObjectMapper().readValue(data, InternshipAddRequest.class);
        InternshipDocument internshipDocument = internshipDocumentService.addInternship(file, addRequest, principal.getUserEntity());
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
            @AuthenticationPrincipal UserPrincipal principal
    ) throws IOException {
        MedicalReportAddRequest addRequest = new ObjectMapper().readValue(data, MedicalReportAddRequest.class);
        MedicalReportDocument medicalReportDocument = medicalReportDocumentService.addMedicalReport(file,
                addRequest,
                principal.getUserEntity());
        return new ResponseEntity<>(medicalReportDocument.toDto(), HttpStatus.CREATED);
    }

    @Operation(
            summary = "Modify document category",
            description = "Modifies a specific document category, mainly used to organize uploaded documents. "
    )
    @PatchMapping("/modify-category")
    public ResponseEntity<DocumentDto> modifyCategory(@Valid @RequestBody DocumentModifyCategoryRequest body,
                                                      @AuthenticationPrincipal UserPrincipal principal) {
        Document modifiedDocument = documentService.modifyDocumentCategory(body, principal.getUserEntity());
        return ResponseEntity.ok(modifiedDocument.toDto());
    }

    @Operation(
            summary = "Get file preview",
            description = """
                    Display's a preview of the file linked to database with the specific `UUID`.
                    """
    )
    @GetMapping("/preview")
    public ResponseEntity<byte[]> getFilePreview(@AuthenticationPrincipal UserPrincipal principal,
                                                 @RequestParam UUID uuid) throws IOException {
        DocumentPreview documentPreview = documentService.getDocumentPreview(uuid, principal.getUserEntity());
        if (documentPreview != null) {
            String fileName = documentPreview.fileName();
            byte[] blob = documentPreview.blob();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentLength(blob.length);
            headers.set("Content-Disposition", "inline; filename=" + fileName);
            headers.setContentType(MediaType.APPLICATION_PDF);
            return new ResponseEntity<>(blob, headers, HttpStatus.OK);
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
    public ResponseEntity<byte[]> downloadFiles(@AuthenticationPrincipal UserPrincipal principal,
                                                @Valid @RequestBody DocumentDownloadRequest request) throws IOException {
        byte[] zipData = documentService.getDocumentsZip(request.uuids(), principal.getUserEntity());
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
