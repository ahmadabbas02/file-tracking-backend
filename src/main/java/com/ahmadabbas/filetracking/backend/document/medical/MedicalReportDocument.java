package com.ahmadabbas.filetracking.backend.document.medical;

import com.ahmadabbas.filetracking.backend.document.base.Document;
import com.ahmadabbas.filetracking.backend.document.base.DocumentStatus;
import com.ahmadabbas.filetracking.backend.document.medical.payload.MedicalReportDocumentMapper;
import com.ahmadabbas.filetracking.backend.document.medical.payload.MedicalReportDto;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@SuperBuilder
@Entity
@Table(name = "medical_report_document")
public class MedicalReportDocument extends Document {

    private LocalDate dateOfAbsence;

    @Builder.Default
    @Enumerated(value = EnumType.STRING)
    @Column(name = "approval_status")
    private DocumentStatus.ApprovalStatus medicalReportApprovalStatus = DocumentStatus.ApprovalStatus.PENDING;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MedicalReportDocument that)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(getDateOfAbsence(), that.getDateOfAbsence())
               && getMedicalReportApprovalStatus() == that.getMedicalReportApprovalStatus();
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getDateOfAbsence(), getMedicalReportApprovalStatus());
    }

    @Override
    public String toString() {
        return "MedicalReportDocument{" +
               "dateOfAbsence=" + dateOfAbsence +
               ", approvalStatus=" + medicalReportApprovalStatus +
               '}';
    }

    @Override
    public MedicalReportDto toDto() {
        return MedicalReportDocumentMapper.INSTANCE.toDto(this);
    }
}
