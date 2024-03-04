package com.ahmadabbas.filetracking.backend.document.internship;

import com.ahmadabbas.filetracking.backend.category.Category;
import com.ahmadabbas.filetracking.backend.category.CategoryService;
import com.ahmadabbas.filetracking.backend.document.internship.payload.InternshipAddRequest;
import com.ahmadabbas.filetracking.backend.exception.ResourceNotFoundException;
import com.ahmadabbas.filetracking.backend.student.Student;
import com.ahmadabbas.filetracking.backend.student.StudentService;
import com.ahmadabbas.filetracking.backend.util.AzureBlobService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class InternshipDocumentService {
    private final InternshipDocumentRepository internshipDocumentRepository;
    private final StudentService studentService;
    private final AzureBlobService azureBlobService;
    private final CategoryService categoryService;

    public InternshipDocument getInternshipById(UUID uuid) {
        return internshipDocumentRepository.findById(uuid)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "internship with uuid %s not found".formatted(uuid)
                ));
    }

    public List<InternshipDocument> getAllInternshipsByStudentId(String studentId) {
        return internshipDocumentRepository.findAllByStudentId(studentId);
    }

    @Transactional
    public InternshipDocument saveInternship(MultipartFile file, InternshipAddRequest addRequest) throws IOException {
        log.info("InternshipDocumentService.saveInternship");
        Student student = studentService.getStudent(addRequest.studentId());
        Category category = categoryService.getCategoryByName("Internship");
        String cloudPath = azureBlobService.upload(file, "/" + addRequest.studentId());
        log.info("cloudPath received from uploading file: %s".formatted(cloudPath));
        InternshipDocument internshipDocument = InternshipDocument.builder()
                .category(category)
                .title(addRequest.title())
                .description(addRequest.description())
                .numberOfWorkingDays(addRequest.numberOfWorkingDays())
                .path(cloudPath)
                .student(student)
                .build();
        return internshipDocumentRepository.save(internshipDocument);
    }
}
