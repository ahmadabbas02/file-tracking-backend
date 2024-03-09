package com.ahmadabbas.filetracking.backend.document.base;

import com.ahmadabbas.filetracking.backend.document.base.payload.DocumentAddRequest;
import com.ahmadabbas.filetracking.backend.document.base.payload.DocumentDownloadRequest;
import com.ahmadabbas.filetracking.backend.document.base.payload.DocumentDto;
import com.ahmadabbas.filetracking.backend.document.base.payload.DocumentModifyCategoryRequest;
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
import com.ahmadabbas.filetracking.backend.document.petition.comment.Comment;
import com.ahmadabbas.filetracking.backend.document.petition.comment.CommentService;
import com.ahmadabbas.filetracking.backend.document.petition.comment.payload.CommentAddRequest;
import com.ahmadabbas.filetracking.backend.document.petition.comment.payload.CommentDto;
import com.ahmadabbas.filetracking.backend.document.petition.comment.payload.CommentMapper;
import com.ahmadabbas.filetracking.backend.document.petition.payload.PetitionDocumentAddRequest;
import com.ahmadabbas.filetracking.backend.document.petition.payload.PetitionDocumentDto;
import com.ahmadabbas.filetracking.backend.user.User;
import com.ahmadabbas.filetracking.backend.util.payload.PaginatedResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
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
            @RequestBody CommentAddRequest addRequest
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
    @GetMapping
    public ResponseEntity<PaginatedResponse<DocumentDto>> getAllDocuments(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "1", required = false) int pageNo,
            @RequestParam(defaultValue = "10", required = false) int pageSize,
            @RequestParam(defaultValue = "uploadedAt", required = false) String sortBy,
            @RequestParam(defaultValue = "desc", required = false) String order,
            @RequestParam(defaultValue = "-1", required = false) String studentId,
            @RequestParam(defaultValue = "", required = false) List<Long> categoryIds,
            @RequestParam(defaultValue = "", required = false) List<Long> parentCategoryIds
    ) {
        if (!studentId.equals("-1")) {
            return ResponseEntity.ok(
                    documentService.getAllDocuments(user, pageNo, pageSize, sortBy, order, studentId, categoryIds, parentCategoryIds)
            );
        }
        return ResponseEntity.ok(documentService.getAllDocuments(user, pageNo, pageSize, sortBy, order, categoryIds, parentCategoryIds));
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
                ContactDocumentAddRequest contactAddRequest = new ObjectMapper().readValue(data, ContactDocumentAddRequest.class);
                ContactDocument contactDocument = contactDocumentService.addContactDocument(contactAddRequest, loggedInUser);
                return new ResponseEntity<>(contactDocument.toDto(), HttpStatus.CREATED);
            case "petition":
                PetitionDocumentAddRequest petitionDocumentAddRequest = new ObjectMapper().readValue(data, PetitionDocumentAddRequest.class);
                PetitionDocument petitionDocument = petitionDocumentService.addPetitionDocument(petitionDocumentAddRequest, loggedInUser);
                return new ResponseEntity<>(petitionDocument.toDto(), HttpStatus.CREATED);
            case "internship":
                InternshipAddRequest internshipAddRequest = new ObjectMapper().readValue(data, InternshipAddRequest.class);
                InternshipDocument internshipDocument = internshipDocumentService.addInternship(file, internshipAddRequest, loggedInUser);
                return new ResponseEntity<>(internshipDocument.toDto(), HttpStatus.CREATED);
            case "medical":
                MedicalReportAddRequest medicalReportAddRequest = new ObjectMapper().readValue(data, MedicalReportAddRequest.class);
                MedicalReportDocument medicalReportDocument = medicalReportDocumentService.addMedicalReport(file, medicalReportAddRequest, loggedInUser);
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
            @RequestBody ContactDocumentAddRequest addRequest,
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
            @RequestBody PetitionDocumentAddRequest addRequest,
            @AuthenticationPrincipal User user
    ) {
        var petitionDocument = petitionDocumentService.addPetitionDocument(addRequest, user);
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
        MedicalReportDocument medicalReportDocument = medicalReportDocumentService.addMedicalReport(file, addRequest, loggedInUser);
        return new ResponseEntity<>(medicalReportDocument.toDto(), HttpStatus.CREATED);
    }

    @Operation(
            summary = "Modify document category",
            description = "Modifies a specific document category, mainly used to organize uploaded documents. "
    )
    @PatchMapping(value = "/modify-category")
    public ResponseEntity<DocumentDto> modifyCategory(@RequestBody DocumentModifyCategoryRequest body,
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
                                                @RequestBody DocumentDownloadRequest request) throws IOException {
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
