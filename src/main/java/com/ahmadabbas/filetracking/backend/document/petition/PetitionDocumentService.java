package com.ahmadabbas.filetracking.backend.document.petition;

import com.ahmadabbas.filetracking.backend.category.Category;
import com.ahmadabbas.filetracking.backend.category.CategoryService;
import com.ahmadabbas.filetracking.backend.document.petition.payload.PetitionDocumentAddRequest;
import com.ahmadabbas.filetracking.backend.student.Student;
import com.ahmadabbas.filetracking.backend.student.StudentService;
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
public class PetitionDocumentService {
    private final PetitionDocumentRepository petitionDocumentRepository;
    private final StudentService studentService;
    private final CategoryService categoryService;
    @Qualifier("webApplicationContext")
    private final ResourceLoader resourceLoader;
    private final AzureBlobService azureBlobService;

    @Transactional
    public PetitionDocument addPetitionDocument(PetitionDocumentAddRequest addRequest, User loggedInUser) {
        if (!loggedInUser.isStudent()) {
            throw new AccessDeniedException("not authorized, only students can do this.");
        }
        log.debug("PetitionDocumentService.addPetitionDocument");
        Student student = studentService.getStudentByUserId(loggedInUser.getId());
        Category category;
        if (addRequest.categoryId() != null) {
            category = categoryService.getCategoryWithDeletionFilter(addRequest.categoryId(), loggedInUser, false);
        } else {
            category = categoryService.getCategoryByName("Petition");
        }
        try {
            File filledPdf = generatedFilledPetition(addRequest,
                    student.getId(),
                    loggedInUser.getFirstName(),
                    loggedInUser.getLastName());
            if (filledPdf == null) {
                throw new RuntimeException("couldn't generate petition form pdf.");
            }
            String cloudPath = azureBlobService.upload(filledPdf, student.getId(), category.getName(), addRequest.title());
            log.debug("cloudPath = {}", cloudPath);
            if (!filledPdf.delete()) {
                log.warn("Failed to delete temp file @ {}", filledPdf.getAbsolutePath());
            }
            PetitionDocument document = PetitionDocument.builder()
                    .title(addRequest.title())
                    .email(addRequest.email())
                    .subject(addRequest.subject())
                    .description(addRequest.description())
                    .category(category)
                    .student(student)
                    .path(cloudPath)
                    .build();
            return petitionDocumentRepository.save(document);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private File generatedFilledPetition(PetitionDocumentAddRequest addRequest,
                                         String studentId,
                                         String firstName,
                                         String lastName) throws IOException {
        Resource pdfResource = resourceLoader.getResource("classpath:static/Student Petition Fillable.pdf");
        PdfReader reader = new PdfReader(pdfResource.getInputStream());
        File outputFile = File.createTempFile(studentId, ".pdf");
        try (PdfStamper stamp = new PdfStamper(reader, new FileOutputStream(outputFile))) {
            AcroFields form = stamp.getAcroFields();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate localDate = LocalDate.now(ZoneId.of("Europe/Athens"));
            form.setField("studentNumber", studentId);
            form.setField("department", "Computer Engineering");
            form.setField("name", firstName);
            form.setField("surname", lastName);
            form.setField("email", addRequest.email());
            form.setField("phoneNumber", addRequest.phoneNumber());
            form.setField("subject", addRequest.subject());
            form.setField("date", localDate.format(formatter));
            form.setField("signature", firstName + " " + lastName);
            form.setField("reason", addRequest.reasoning());
            stamp.setFormFlattening(true);
            return outputFile;
        }
    }
}
