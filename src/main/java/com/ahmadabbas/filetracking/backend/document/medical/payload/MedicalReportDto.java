package com.ahmadabbas.filetracking.backend.document.medical.payload;

import com.ahmadabbas.filetracking.backend.document.base.DocumentStatus;
import com.ahmadabbas.filetracking.backend.document.base.payload.DocumentDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Data
public class MedicalReportDto extends DocumentDto implements Serializable {
    LocalDate dateOfAbsence;
    DocumentStatus.ApprovalStatus approvalStatus;
}