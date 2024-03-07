package com.ahmadabbas.filetracking.backend.document.medical;

import com.ahmadabbas.filetracking.backend.document.base.Document;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.StringJoiner;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@Entity
public class MedicalReportDocument extends Document {

    private LocalDateTime dateOfAbsence;

    private String note;

    @Enumerated(value = EnumType.STRING)
    private MedicalReportStatus medicalReportStatus;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MedicalReportDocument that)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(getDateOfAbsence(), that.getDateOfAbsence()) && Objects.equals(getNote(), that.getNote()) && getMedicalReportStatus() == that.getMedicalReportStatus();
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getDateOfAbsence(), getNote(), getMedicalReportStatus());
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", MedicalReportDocument.class.getSimpleName() + "[", "]")
                .add("dateOfAbsence=" + dateOfAbsence)
                .add("note='" + note + "'")
                .add("medicalReportStatus=" + medicalReportStatus)
                .toString();
    }
}
