package com.ahmadabbas.filetracking.backend.document.contact;

import com.ahmadabbas.filetracking.backend.category.Category;
import com.ahmadabbas.filetracking.backend.category.CategoryService;
import com.ahmadabbas.filetracking.backend.document.contact.payload.ContactDocumentAddRequest;
import com.ahmadabbas.filetracking.backend.exception.ResourceNotFoundException;
import com.ahmadabbas.filetracking.backend.student.Student;
import com.ahmadabbas.filetracking.backend.student.StudentService;
import com.ahmadabbas.filetracking.backend.user.Role;
import com.ahmadabbas.filetracking.backend.user.User;
import com.ahmadabbas.filetracking.backend.util.AzureBlobService;
import com.lowagie.text.pdf.AcroFields;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Service
@Transactional(readOnly = true)
@Slf4j
@RequiredArgsConstructor
public class ContactDocumentService {
    private final ContactDocumentRepository contactDocumentRepository;
    private final CategoryService categoryService;
    private final StudentService studentService;
    @Qualifier("webApplicationContext")
    private final ResourceLoader resourceLoader;
    private final AzureBlobService azureBlobService;

    public ContactDocument getContactDocument(String studentId) {
        return contactDocumentRepository.findByStudentId(studentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "contact document for student id `%s` not found".formatted(studentId)
                ));
    }

    @Transactional
    public ContactDocument addContactDocument(ContactDocumentAddRequest addRequest, User loggedInUser) {
        if (!loggedInUser.getRoles().contains(Role.STUDENT)) {
            throw new AccessDeniedException("not authorized, only students can do this.");
        }
        log.debug("ContactDocumentService.addContactDocument");
        Student student = studentService.getStudentByUserId(loggedInUser.getId());
        Category category;
        if (addRequest.categoryId() != null) {
            category = categoryService.getCategoryWithDeletionFilter(addRequest.categoryId(), loggedInUser, false);
        } else {
            category = categoryService.getCategoryByName("Contact Form");
        }
        try {
            File filledPdf = generateContactFilledPdf(addRequest, student, loggedInUser.getFullName());
            if (filledPdf == null) {
                throw new RuntimeException("couldn't generate contact form pdf.");
            }
            String cloudPath = azureBlobService.upload(filledPdf, student.getId(), category.getName(), addRequest.title());
            log.debug("cloudPath = {}", cloudPath);
            if (!filledPdf.delete()) {
                log.warn("Failed to delete temp file @ {}", filledPdf.getAbsolutePath());
            }
            ContactDocument document = ContactDocument.builder()
                    .description(addRequest.description())
                    .email(addRequest.email())
                    .phoneNumber(addRequest.phoneNumber())
                    .homeNumber(addRequest.homeNumber())
                    .emergencyName(addRequest.emergencyName())
                    .emergencyPhoneNumber(addRequest.emergencyPhoneNumber())
                    .title(addRequest.title())
                    .category(category)
                    .student(student)
                    .path(cloudPath)
                    .build();
            return contactDocumentRepository.save(document);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private File generateContactFilledPdf(ContactDocumentAddRequest addRequest, Student student, String fullName) throws IOException {
        Resource resource = resourceLoader.getResource("classpath:static/Student Information Fillable Form.pdf");
        PdfReader reader = new PdfReader(resource.getInputStream());
        File outputFile = File.createTempFile(student.getId(), ".pdf");
        try (PdfStamper stamp = new PdfStamper(reader, new FileOutputStream(outputFile))) {
            AcroFields form = stamp.getAcroFields();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate localDate = LocalDate.now(ZoneId.of("Europe/Athens"));
            form.setField("program", student.getProgram().toLowerCase());
            form.setField("date", localDate.format(formatter));
            form.setField("studentNumber", student.getId());
            form.setField("studentName", fullName);
            form.setField("studentEmail", addRequest.email());
            form.setField("homeTeleNumber", addRequest.homeNumber());
            form.setField("mobileTeleNumber", addRequest.phoneNumber());
            form.setField("emergencyName", addRequest.emergencyName());
            form.setField("emergencyPhoneNumber", addRequest.emergencyPhoneNumber());
            stamp.setFormFlattening(true);
            return outputFile;
        }
    }
}
