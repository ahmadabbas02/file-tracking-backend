package com.ahmadabbas.filetracking.backend;

import com.ahmadabbas.filetracking.backend.advisor.Advisor;
import com.ahmadabbas.filetracking.backend.advisor.AdvisorService;
import com.ahmadabbas.filetracking.backend.advisor.payload.AdvisorRegistrationRequest;
import com.ahmadabbas.filetracking.backend.document.base.Document;
import com.ahmadabbas.filetracking.backend.document.base.DocumentRepository;
import com.ahmadabbas.filetracking.backend.document.category.Category;
import com.ahmadabbas.filetracking.backend.document.category.CategoryRepository;
import com.ahmadabbas.filetracking.backend.document.medical.MedicalReportDocument;
import com.ahmadabbas.filetracking.backend.document.medical.MedicalReportStatus;
import com.ahmadabbas.filetracking.backend.student.Student;
import com.ahmadabbas.filetracking.backend.student.StudentService;
import com.ahmadabbas.filetracking.backend.student.payload.StudentRegistrationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Date;

@RequiredArgsConstructor
@Component
public class DataLoader implements ApplicationRunner {
    private final CategoryRepository categoryRepository;
    private final AdvisorService advisorService;
    private final StudentService studentService;
    private final DocumentRepository documentRepository;

    @Override
    public void run(ApplicationArguments args) {
        // Add category and sub categories
        Category savedMainCategory = categoryRepository.save(
                Category.builder()
                        .parentCategoryId((long) -1)
                        .name("Main Cat. 1")
                        .build()
        );
        Category savedSubCategory1 = categoryRepository.save(
                Category.builder()
                        .parentCategoryId(savedMainCategory.getCategoryId())
                        .name("Sub Cat. 1")
                        .build()
        );
        Category savedSubCategory2 = categoryRepository.save(
                Category.builder()
                        .parentCategoryId(savedMainCategory.getCategoryId())
                        .name("Sub Cat. 2")
                        .build()
        );

        // Add advisor
        Advisor savedAdvisor = advisorService.addAdvisor(
                new AdvisorRegistrationRequest(
                        "Duygu",
                        "duygu@email.com",
                        "duygu"
                )
        );

        // Add student
        Student student1 = studentService.addStudent(
                new StudentRegistrationRequest(
                        "Ahmad",
                        "ahmad@email.com",
                        "ahmad",
                        "CMSE",
                        (short) 4,
                        "",
                        savedAdvisor.getId()
                )
        );

        Document document = new MedicalReportDocument(
                Date.from(Instant.now()),
                "Test",
                MedicalReportStatus.APPROVED
        );

        document.setStudent(student1);
        document.setCategory(savedSubCategory1);

        documentRepository.save(
                document
        );
    }
}
