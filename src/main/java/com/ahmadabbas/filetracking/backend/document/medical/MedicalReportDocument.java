package com.ahmadabbas.filetracking.backend.document.medical;

import com.ahmadabbas.filetracking.backend.document.base.Document;
import com.ahmadabbas.filetracking.backend.document.medical.payload.MedicalReportDocumentMapper;
import com.ahmadabbas.filetracking.backend.document.medical.payload.MedicalReportDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.Objects;
import java.util.StringJoiner;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@SuperBuilder
@Entity
public class MedicalReportDocument extends Document {

    private LocalDate dateOfAbsence;

    @Column(columnDefinition = "boolean default false")
    @Builder.Default
    private boolean isApproved = false;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MedicalReportDocument that)) return false;
        if (!super.equals(o)) return false;
        return isApproved() == that.isApproved() && Objects.equals(getDateOfAbsence(), that.getDateOfAbsence());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getDateOfAbsence(), isApproved());
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", MedicalReportDocument.class.getSimpleName() + "[", "]")
                .add("dateOfAbsence=" + dateOfAbsence)
                .add("isApproved=" + isApproved)
                .toString();
    }

    @Override
    public MedicalReportDto toDto() {
        return MedicalReportDocumentMapper.INSTANCE.toDto(this);
    }
}
