package com.ahmadabbas.filetracking.backend;

import com.ahmadabbas.filetracking.backend.advisor.AdvisorService;
import com.ahmadabbas.filetracking.backend.category.CategoryService;
import com.ahmadabbas.filetracking.backend.document.base.repository.DocumentRepository;
import com.ahmadabbas.filetracking.backend.student.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

@RequiredArgsConstructor
//@Component
public class DataLoader implements ApplicationRunner {
    private final CategoryService categoryService;
    private final AdvisorService advisorService;
    private final StudentService studentService;
    private final DocumentRepository documentRepository;

    @Override
    public void run(ApplicationArguments args) {
//        Faker faker = new Faker();
//
//        // Add category and sub categories
//        Category savedMainCategory = categoryService.createCategory(
//                Category.builder()
//                        .name("Main Cat. 1")
//                        .build()
//        );
//
//        Category savedSubCategory1 = categoryService.createCategory(
//                Category.builder()
//                        .parentCategoryId(savedMainCategory.getCategoryId())
//                        .name("Sub Cat. 1")
//                        .build()
//        );
//
//        Category savedSubCategory2 = categoryService.createCategory(
//                Category.builder()
//                        .parentCategoryId(savedMainCategory.getCategoryId())
//                        .name("Sub Cat. 2")
//                        .build()
//        );
//
//        Category medicalReportCategory = categoryService.createCategory(
//                Category.builder()
//                        .name("Medical Reports")
//                        .build()
//        );
//
//        Category contactCategory = categoryService.createCategory(
//                Category.builder()
//                        .name("Contact Forms")
//                        .build()
//        );
//
//        // Add advisor
//        Advisor savedAdvisor = advisorService.addAdvisor(
//                new AdvisorRegistrationRequest(
//                        "Duygu Celik",
//                        "duygu.celik@emu.edu.tr",
//                        "duygu"
//                )
//        );
//
//        // Add student
//        Student student1 = studentService.addStudent(
//                new StudentRegistrationRequest(
//                        "Ahmad",
//                        "ahmad@email.com",
//                        "ahmad",
//                        "CMSE",
//                        (short) 4,
//                        "picture url",
//                        savedAdvisor.getId()
//                )
//        );
//
//        Student student2 = studentService.addStudent(
//                new StudentRegistrationRequest(
//                        "Hussein",
//                        "hussein@email.com",
//                        "hussein",
//                        "CMSE",
//                        (short) 4,
//                        "picture url",
//                        savedAdvisor.getId()
//                )
//        );
//
//        for (int i = 0; i < 10; i++) {
//            studentService.addStudent(
//                    new StudentRegistrationRequest(
//                            faker.name().fullName(),
//                            faker.internet().emailAddress(),
//                            "123Fake321",
//                            faker.commerce().department(),
//                            Short.parseShort(String.valueOf(faker.number().randomDigitNotZero())),
//                            "",
//                            savedAdvisor.getId()
//                    )
//            );
//        }
//
//        Document medicalReportDocument = new MedicalReportDocument(
//                LocalDateTime.from(Instant.now()),
//                "Test",
//                MedicalReportStatus.APPROVED
//        );
//        medicalReportDocument.setStudent(student1);
//        medicalReportDocument.setCategory(medicalReportCategory);
//        medicalReportDocument.setPath("");
//        documentRepository.save(medicalReportDocument);
//
//        Document medicalReportDocument2 = new MedicalReportDocument(
//                LocalDateTime.from(Instant.now()),
//                "Test2",
//                MedicalReportStatus.REJECTED
//        );
//        medicalReportDocument2.setStudent(student2);
//        medicalReportDocument2.setCategory(medicalReportCategory);
//        medicalReportDocument2.setPath("");
//        documentRepository.save(medicalReportDocument2);
//
//        Document medicalReportDocument3 = new MedicalReportDocument(
//                LocalDateTime.from(Instant.now().minus(7, ChronoUnit.DAYS)),
//                "Test3",
//                MedicalReportStatus.REJECTED
//        );
//        medicalReportDocument3.setStudent(student1);
//        medicalReportDocument3.setCategory(medicalReportCategory);
//        medicalReportDocument3.setPath("");
//        documentRepository.save(medicalReportDocument3);
//
//        Document contactDocument = new ContactDocument(
//                "email@mail.com",
//                "+905331233211",
//                "Emergency Name",
//                "+905331123321"
//        );
//        contactDocument.setStudent(student1);
//        contactDocument.setCategory(contactCategory);
//        contactDocument.setPath("");
//        documentRepository.save(contactDocument);
    }
}
