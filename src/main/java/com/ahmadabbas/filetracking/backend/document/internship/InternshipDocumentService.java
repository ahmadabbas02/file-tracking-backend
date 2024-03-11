package com.ahmadabbas.filetracking.backend.document.internship;

import com.ahmadabbas.filetracking.backend.category.Category;
import com.ahmadabbas.filetracking.backend.category.CategoryService;
import com.ahmadabbas.filetracking.backend.document.internship.payload.InternshipAddRequest;
import com.ahmadabbas.filetracking.backend.student.Student;
import com.ahmadabbas.filetracking.backend.student.StudentService;
import com.ahmadabbas.filetracking.backend.user.Role;
import com.ahmadabbas.filetracking.backend.user.User;
import com.ahmadabbas.filetracking.backend.util.AzureBlobService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class InternshipDocumentService {
    private final InternshipDocumentRepository internshipDocumentRepository;
    private final StudentService studentService;
    private final AzureBlobService azureBlobService;
    private final CategoryService categoryService;

    @Transactional
    public InternshipDocument addInternship(MultipartFile file,
                                            InternshipAddRequest addRequest,
                                            User loggedInUser) throws IOException {
        if (!loggedInUser.getRoles().contains(Role.SECRETARY)) {
            throw new AccessDeniedException("not authorized, only secretary can do this.");
        }
        log.info("InternshipDocumentService.saveInternship");
        Student student = studentService.getStudent(addRequest.studentId(), loggedInUser);
        Category category = categoryService.getCategoryByName("Internship");
        String cloudPath = azureBlobService.upload(file, addRequest.studentId(), category.getName(), addRequest.title());
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
