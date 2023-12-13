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

}
