package com.ahmadabbas.filetracking.backend.student.payload;

import com.ahmadabbas.filetracking.backend.document.internship.InternshipStatus;
import com.ahmadabbas.filetracking.backend.student.Student;

import java.io.Serializable;

/**
 * DTO for {@link Student}
 */
public record StudentUserDto(
        String id,
        String program,
        Short year,
        InternshipStatus.CompletionStatus internshipCompletionStatus,
        InternshipStatus.PaymentStatus paymentStatus
) implements Serializable {
}