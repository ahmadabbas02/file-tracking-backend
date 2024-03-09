package com.ahmadabbas.filetracking.backend.document.medical;

import com.ahmadabbas.filetracking.backend.category.Category;
import com.ahmadabbas.filetracking.backend.category.CategoryService;
import com.ahmadabbas.filetracking.backend.document.medical.payload.MedicalReportAddRequest;
import com.ahmadabbas.filetracking.backend.exception.APIException;
import com.ahmadabbas.filetracking.backend.student.Student;
import com.ahmadabbas.filetracking.backend.student.StudentService;
import com.ahmadabbas.filetracking.backend.user.User;
import com.ahmadabbas.filetracking.backend.util.AzureBlobService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
    public MedicalReportDocument saveMedicalReport(MultipartFile file,
                                                   MedicalReportAddRequest addRequest,
                                                   User loggedInUser) throws IOException {
        LocalDate localDate;
        try {
            localDate = LocalDate.parse(addRequest.dateOfAbsence());
        } catch (DateTimeParseException exception) {
            throw new APIException(HttpStatus.BAD_REQUEST,
                    "Failed to parse date. It should be in the format `yyyy-MM-dd` like `2024-03-09`");
        }

        Student student = studentService.getStudentByUserId(loggedInUser.getId());
        Category medicalCategory = categoryService.getCategoryByName("Medical Reports");
        String cloudPath = azureBlobService.upload(file, "/" + student.getId());
        log.info("cloudPath received from uploading file: %s".formatted(cloudPath));
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
