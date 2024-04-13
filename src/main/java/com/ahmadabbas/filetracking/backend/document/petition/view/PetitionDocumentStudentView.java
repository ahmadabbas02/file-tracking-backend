package com.ahmadabbas.filetracking.backend.document.petition.view;

import com.ahmadabbas.filetracking.backend.document.base.DocumentStatus;
import com.ahmadabbas.filetracking.backend.document.base.view.DocumentStudentView;
import com.ahmadabbas.filetracking.backend.document.petition.PetitionDocument;
import com.blazebit.persistence.view.EntityView;
import com.blazebit.persistence.view.FlushMode;
import com.blazebit.persistence.view.UpdatableEntityView;

@EntityView(PetitionDocument.class)
@UpdatableEntityView(mode = FlushMode.PARTIAL)
public interface PetitionDocumentStudentView extends DocumentStudentView {
    DocumentStatus.ApprovalStatus getApprovalStatus();

    void setApprovalStatus(DocumentStatus.ApprovalStatus approvalStatus);
}