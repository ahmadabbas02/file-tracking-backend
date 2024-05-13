package com.ahmadabbas.filetracking.backend.student.payload;

import com.ahmadabbas.filetracking.backend.advisor.payload.AdvisorDto;
import com.ahmadabbas.filetracking.backend.document.base.DocumentStatus;
import com.ahmadabbas.filetracking.backend.student.EducationStatus;

import java.io.Serializable;

public record StudentDto(
        String id,
        String fullName,
        String firstName,
        String lastName,
        String email,
        String phoneNumber,
        String program,
        Short year,
        String picture,
        AdvisorDto advisor,
        DocumentStatus.InternshipCompletionStatus internshipCompletionStatus,
        DocumentStatus.InternshipPaymentStatus paymentStatus,
        EducationStatus educationStatus
) implements Serializable {
}