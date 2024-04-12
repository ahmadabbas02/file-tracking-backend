package com.ahmadabbas.filetracking.backend.student.payload;

import com.ahmadabbas.filetracking.backend.document.base.DocumentStatus;
import com.ahmadabbas.filetracking.backend.student.Student;

import java.io.Serializable;

/**
 * DTO for {@link Student}
 */
public record StudentUserDto(
        String id,
        String program,
        Short year,
        DocumentStatus.InternshipCompletionStatus internshipCompletionStatus,
        DocumentStatus.InternshipPaymentStatus paymentStatus
) implements Serializable {
}