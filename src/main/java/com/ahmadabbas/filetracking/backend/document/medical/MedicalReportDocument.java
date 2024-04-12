package com.ahmadabbas.filetracking.backend.document.medical;

import com.ahmadabbas.filetracking.backend.document.base.Document;
import com.ahmadabbas.filetracking.backend.document.base.DocumentStatus;
import com.ahmadabbas.filetracking.backend.document.medical.payload.MedicalReportDocumentMapper;
import com.ahmadabbas.filetracking.backend.document.medical.payload.MedicalReportDto;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
public class MedicalReportDocument extends Document {

    private LocalDate dateOfAbsence;

    @Builder.Default
    @Enumerated(value = EnumType.STRING)
    private DocumentStatus.ApprovalStatus approvalStatus = DocumentStatus.ApprovalStatus.PENDING;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MedicalReportDocument that)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(getDateOfAbsence(), that.getDateOfAbsence())
               && getApprovalStatus() == that.getApprovalStatus();
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getDateOfAbsence(), getApprovalStatus());
    }

    @Override
    public String toString() {
        return "MedicalReportDocument{" +
               "dateOfAbsence=" + dateOfAbsence +
               ", approvalStatus=" + approvalStatus +
               '}';
    }

    @Override
    public MedicalReportDto toDto() {
        return MedicalReportDocumentMapper.INSTANCE.toDto(this);
    }
}
