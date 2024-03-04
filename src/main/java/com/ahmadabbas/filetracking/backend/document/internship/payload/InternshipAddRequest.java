package com.ahmadabbas.filetracking.backend.document.internship.payload;

public record InternshipAddRequest(String title, String description, int numberOfWorkingDays,
                                   String studentId) {
}
