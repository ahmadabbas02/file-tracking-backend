package com.ahmadabbas.filetracking.backend.document.base;

import com.ahmadabbas.filetracking.backend.document.payload.DocumentAddRequest;
import com.ahmadabbas.filetracking.backend.document.payload.DocumentDto;
import com.ahmadabbas.filetracking.backend.document.payload.DocumentMapper;
import com.ahmadabbas.filetracking.backend.document.payload.DocumentModifyCategoryRequest;
import com.ahmadabbas.filetracking.backend.util.payload.PaginatedResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/documents")
public class DocumentController {

    private final DocumentService documentService;

    @Operation(
            summary = "Get all documents",
            description = """
                    Returns a pagination result of all documents in the database
                    sorted by default on `uploadedAt` and `descending` order.
                    """
    )
    @GetMapping
    public ResponseEntity<PaginatedResponse<DocumentDto>> getAllDocuments(
            @RequestParam(defaultValue = "1", required = false) int pageNo,
            @RequestParam(defaultValue = "10", required = false) int pageSize,
            @RequestParam(defaultValue = "uploadedAt", required = false) String sortBy,
            @RequestParam(defaultValue = "desc", required = false) String order,
            @RequestParam(defaultValue = "-1", required = false) String studentId
    ) {
        if (!studentId.equals("-1")) {
            return ResponseEntity.ok(documentService.getAllDocuments(pageNo, pageSize, sortBy, order, studentId));
        }
        return ResponseEntity.ok(documentService.getAllDocuments(pageNo, pageSize, sortBy, order));
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

        return ResponseEntity.ok(DocumentMapper.INSTANCE.toDto(uploadedDocument));
    }

    @Operation(
            summary = "Modify document category",
            description = "Modifies a specific document category, mainly used to organize uploaded documents. "
    )
    @PatchMapping(value = "/modify-category")
    public ResponseEntity<DocumentDto> modifyCategory(@RequestBody DocumentModifyCategoryRequest body) {
        Document modifiedDocument = documentService.modifyDocumentCategory(body);
        return ResponseEntity.ok(DocumentMapper.INSTANCE.toDto(modifiedDocument));
    }

//    @GetMapping("/download")
//    public ResponseEntity<byte[]> getFile(@RequestParam String fileName) throws IOException {
//        byte[] data = azureBlobService.downloadBlob(fileName).readAllBytes();
//        if (data != null) {
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentLength(data.length);
//            headers.set("Content-Disposition", "attachment; filename=" + fileName);
//            return new ResponseEntity<>(data, headers, HttpStatus.OK);
//        } else {
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        }
//    }

    @Operation(
            summary = "Get file preview",
            description = """
                    Display's a preview of the file linked to database with the specific `UUID`.
                    """
    )
    @GetMapping("/preview")
    public ResponseEntity<byte[]> getFilePreview(@RequestParam UUID uuid) throws IOException {
        byte[] data = documentService.getDocumentPreview(uuid);
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
}
