package com.ahmadabbas.filetracking.backend.student.payload;

import com.ahmadabbas.filetracking.backend.document.internship.InternshipStatus;
import com.ahmadabbas.filetracking.backend.student.Student;

import java.io.Serializable;

/**
 * DTO for {@link Student}
 */
public record StudentDto(String id, String name, String email, String phoneNumber, String program, Short year,
                         String picture, String advisorName,
                         InternshipStatus.CompletionStatus internshipCompletionStatus,
                         InternshipStatus.PaymentStatus paymentStatus) implements Serializable {
}