package com.ahmadabbas.filetracking.backend.document.medical.payload;


public record MedicalReportAddRequest(String title, String description, String dateOfAbsence) {
}
