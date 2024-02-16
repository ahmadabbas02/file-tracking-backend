package com.ahmadabbas.filetracking.backend.document.medical;

import com.ahmadabbas.filetracking.backend.document.base.Document;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.Objects;
import java.util.StringJoiner;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@Entity
public class MedicalReportDocument extends Document {

    private Date dateOfAbsence;

    private String note;

    @Enumerated(value = EnumType.STRING)
    private MedicalReportStatus medicalReportStatus;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MedicalReportDocument that)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(dateOfAbsence, that.dateOfAbsence)
                && Objects.equals(note, that.note)
                && medicalReportStatus == that.medicalReportStatus;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), dateOfAbsence, note, medicalReportStatus);
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
