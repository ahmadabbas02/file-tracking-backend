package com.ahmadabbas.filetracking.backend.student.payload;

import com.ahmadabbas.filetracking.backend.document.base.DocumentStatus;

import java.io.Serializable;

public record StudentUserDto(
        String id,
        String program,
        Short year,
        DocumentStatus.InternshipCompletionStatus internshipCompletionStatus,
        DocumentStatus.InternshipPaymentStatus paymentStatus
) implements Serializable {
}