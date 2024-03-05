package com.ahmadabbas.filetracking.backend.document.base;

import com.ahmadabbas.filetracking.backend.document.base.payload.DocumentAddRequest;
import com.ahmadabbas.filetracking.backend.document.base.payload.DocumentDto;
import com.ahmadabbas.filetracking.backend.document.base.payload.DocumentMapper;
import com.ahmadabbas.filetracking.backend.document.base.payload.DocumentModifyCategoryRequest;
import com.ahmadabbas.filetracking.backend.document.contact.ContactDocument;
import com.ahmadabbas.filetracking.backend.document.contact.ContactDocumentService;
import com.ahmadabbas.filetracking.backend.document.contact.payload.ContactDocumentAddRequest;
import com.ahmadabbas.filetracking.backend.document.contact.payload.ContactDocumentDto;
import com.ahmadabbas.filetracking.backend.document.contact.payload.ContactDocumentMapper;
import com.ahmadabbas.filetracking.backend.document.internship.InternshipDocument;
import com.ahmadabbas.filetracking.backend.document.internship.InternshipDocumentService;
import com.ahmadabbas.filetracking.backend.document.internship.payload.InternshipAddRequest;
import com.ahmadabbas.filetracking.backend.document.internship.payload.InternshipDocumentDto;
import com.ahmadabbas.filetracking.backend.document.internship.payload.InternshipDocumentMapper;
import com.ahmadabbas.filetracking.backend.util.payload.PaginatedResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/documents")
public class DocumentController {

    private final DocumentService documentService;
    private final DocumentMapper documentMapper;
    private final ContactDocumentService contactDocumentService;
    private final ContactDocumentMapper contactDocumentMapper;
    private final InternshipDocumentService internshipDocumentService;
    private final InternshipDocumentMapper internshipDocumentMapper;

    @Operation(
            summary = "Get all documents",
            description = """
                    Returns a pagination result of all documents in the database
                    sorted by default on `uploadedAt` and `descending` order.
                    """
    )
    @GetMapping
    public ResponseEntity<PaginatedResponse<DocumentDto>> getAllDocuments(
            Authentication authentication,
            @RequestParam(defaultValue = "1", required = false) int pageNo,
            @RequestParam(defaultValue = "10", required = false) int pageSize,
            @RequestParam(defaultValue = "uploadedAt", required = false) String sortBy,
            @RequestParam(defaultValue = "desc", required = false) String order,
            @RequestParam(defaultValue = "-1", required = false) String studentId,
            @RequestParam(defaultValue = "-1", required = false) Long categoryId,
            @RequestParam(defaultValue = "-1", required = false) Long parentCategoryId
    ) {
        if (!studentId.equals("-1")) {
            return ResponseEntity.ok(
                    documentService.getAllDocuments(authentication, pageNo, pageSize, sortBy, order, studentId, categoryId, parentCategoryId)
            );
        }
        return ResponseEntity.ok(documentService.getAllDocuments(authentication, pageNo, pageSize, sortBy, order, categoryId, parentCategoryId));
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
            @RequestPart("data") String data
    ) throws IOException {
        DocumentAddRequest addRequest = new ObjectMapper().readValue(data, DocumentAddRequest.class);
        Document uploadedDocument = documentService.uploadDocument(file, addRequest);
        return new ResponseEntity<>(documentMapper.toDto(uploadedDocument), HttpStatus.CREATED);
    }

    @Operation(
            summary = "Modify document category",
            description = "Modifies a specific document category, mainly used to organize uploaded documents. "
    )
    @PatchMapping(value = "/modify-category")
    public ResponseEntity<DocumentDto> modifyCategory(@RequestBody DocumentModifyCategoryRequest body) {
        Document modifiedDocument = documentService.modifyDocumentCategory(body);
        return ResponseEntity.ok(documentMapper.toDto(modifiedDocument));
    }

    @Operation(
            summary = "Get file preview",
            description = """
                    Display's a preview of the file linked to database with the specific `UUID`.
                    """
    )
    @GetMapping("/preview")
    public ResponseEntity<byte[]> getFilePreview(Authentication authentication, @RequestParam UUID uuid) throws IOException {
        byte[] data = documentService.getDocumentPreview(authentication, uuid);
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
            summary = "Get contact document",
            description = """
                    Returns the student's contact document details.
                    """
    )
    @GetMapping("/contact")
    public ResponseEntity<ContactDocumentDto> getContactDocument(@RequestParam String studentId) {
        ContactDocument contactDocument = contactDocumentService.getContactDocument(studentId);
        return ResponseEntity.ok(contactDocumentMapper.toDto(contactDocument));
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
            Authentication authentication
    ) {
        ContactDocument contactDocument = contactDocumentService.addContactDocument(addRequest, authentication);
        return new ResponseEntity<>(contactDocumentMapper.toDto(contactDocument), HttpStatus.CREATED);
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
            @RequestPart("data") String data
    ) throws IOException {
        InternshipAddRequest addRequest = new ObjectMapper().readValue(data, InternshipAddRequest.class);
        InternshipDocument internshipDocument = internshipDocumentService.saveInternship(file, addRequest);
        return new ResponseEntity<>(internshipDocumentMapper.toDto(internshipDocument), HttpStatus.CREATED);
    }
}
