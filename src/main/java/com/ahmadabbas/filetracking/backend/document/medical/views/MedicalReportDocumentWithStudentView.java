package com.ahmadabbas.filetracking.backend.document.medical.views;

import com.ahmadabbas.filetracking.backend.document.base.DocumentStatus;
import com.ahmadabbas.filetracking.backend.document.base.views.DocumentWithStudentView;
import com.ahmadabbas.filetracking.backend.document.medical.MedicalReportDocument;
import com.blazebit.persistence.view.EntityView;
import com.blazebit.persistence.view.FlushMode;
import com.blazebit.persistence.view.Mapping;
import com.blazebit.persistence.view.UpdatableEntityView;

@EntityView(MedicalReportDocument.class)
@UpdatableEntityView(mode = FlushMode.PARTIAL)
public interface MedicalReportDocumentWithStudentView extends DocumentWithStudentView {
    @Mapping("medicalReportApprovalStatus")
    DocumentStatus.ApprovalStatus getApprovalStatus();

    void setApprovalStatus(DocumentStatus.ApprovalStatus approvalStatus);
}