package com.ahmadabbas.filetracking.backend.document.medical;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicalReportDocumentRepository extends JpaRepository<MedicalReportDocument, Long> {
}