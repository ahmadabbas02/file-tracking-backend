package com.ahmadabbas.filetracking.backend;

import com.ahmadabbas.filetracking.backend.advisor.Advisor;
import com.ahmadabbas.filetracking.backend.advisor.AdvisorService;
import com.ahmadabbas.filetracking.backend.advisor.payload.AdvisorRegistrationRequest;
import com.ahmadabbas.filetracking.backend.document.base.Document;
import com.ahmadabbas.filetracking.backend.document.base.DocumentRepository;
import com.ahmadabbas.filetracking.backend.document.category.Category;
import com.ahmadabbas.filetracking.backend.document.category.CategoryRepository;
import com.ahmadabbas.filetracking.backend.document.contact.ContactDocument;
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
import java.time.temporal.ChronoUnit;
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

        Category medicalReportCategory = categoryRepository.save(
                Category.builder()
                        .name("Medical Reports")
                        .build()
        );

        Category contactCategory = categoryRepository.save(
                Category.builder()
                        .name("Contact Forms")
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
                        "picture url",
                        savedAdvisor.getId()
                )
        );

        Student student2 = studentService.addStudent(
                new StudentRegistrationRequest(
                        "Hussein",
                        "hussein@email.com",
                        "hussein",
                        "CMSE",
                        (short) 4,
                        "picture url",
                        savedAdvisor.getId()
                )
        );

        Document medicalReportDocument = new MedicalReportDocument(
                Date.from(Instant.now()),
                "Test",
                MedicalReportStatus.APPROVED
        );
        medicalReportDocument.setStudent(student1);
        medicalReportDocument.setCategory(medicalReportCategory);
        documentRepository.save(medicalReportDocument);

        Document medicalReportDocument2 = new MedicalReportDocument(
                Date.from(Instant.now()),
                "Test2",
                MedicalReportStatus.REJECTED
        );
        medicalReportDocument2.setStudent(student2);
        medicalReportDocument2.setCategory(medicalReportCategory);
        documentRepository.save(medicalReportDocument2);

        Document medicalReportDocument3 = new MedicalReportDocument(
                Date.from(Instant.now().minus(7, ChronoUnit.DAYS)),
                "Test2",
                MedicalReportStatus.REJECTED
        );
        medicalReportDocument3.setStudent(student1);
        medicalReportDocument3.setCategory(medicalReportCategory);
        documentRepository.save(medicalReportDocument3);

        Document contactDocument = new ContactDocument(
                "email@mail.com",
                "+905331233211",
                "Emergency Name",
                "+905331123321"
        );
        contactDocument.setStudent(student1);
        contactDocument.setCategory(contactCategory);
        documentRepository.save(contactDocument);
    }
}
