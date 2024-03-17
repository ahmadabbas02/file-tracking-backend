package com.ahmadabbas.filetracking.backend.document.medical;

import com.ahmadabbas.filetracking.backend.category.Category;
import com.ahmadabbas.filetracking.backend.category.CategoryService;
import com.ahmadabbas.filetracking.backend.document.medical.payload.MedicalReportAddRequest;
import com.ahmadabbas.filetracking.backend.exception.APIException;
import com.ahmadabbas.filetracking.backend.student.Student;
import com.ahmadabbas.filetracking.backend.student.StudentService;
import com.ahmadabbas.filetracking.backend.user.Role;
import com.ahmadabbas.filetracking.backend.user.User;
import com.ahmadabbas.filetracking.backend.util.AzureBlobService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class MedicalReportDocumentService {
    private final MedicalReportDocumentRepository medicalReportDocumentRepository;
    private final CategoryService categoryService;
    private final StudentService studentService;
    private final AzureBlobService azureBlobService;

    @Transactional
    public MedicalReportDocument addMedicalReport(MultipartFile file,
                                                  MedicalReportAddRequest addRequest,
                                                  User loggedInUser) throws IOException {
        if (!loggedInUser.getRoles().contains(Role.STUDENT)) {
            throw new AccessDeniedException("not authorized, only students can do this.");
        }
        LocalDate localDate;
        try {
            String datePart = addRequest.dateOfAbsence().substring(0, 10);
            localDate = LocalDate.parse(datePart);
        } catch (DateTimeParseException exception) {
            throw new APIException(HttpStatus.BAD_REQUEST,
                    "Failed to parse date. It should be in the format like `2024-03-25T22:00:00.000Z`");
        }

        Student student = studentService.getStudentByUserId(loggedInUser.getId());
        Category medicalCategory = categoryService.getCategoryByName("Medical Report");
        String cloudPath = azureBlobService.upload(file, student.getId(), medicalCategory.getName(), addRequest.title());
        log.debug("cloudPath received from uploading file: %s".formatted(cloudPath));
        MedicalReportDocument document = MedicalReportDocument.builder()
                .title(addRequest.title())
                .description(addRequest.description())
                .category(medicalCategory)
                .dateOfAbsence(localDate)
                .student(student)
                .path(cloudPath)
                .build();
        return medicalReportDocumentRepository.save(document);
    }
}
