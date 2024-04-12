package com.ahmadabbas.filetracking.backend.document.petition.payload;

import com.ahmadabbas.filetracking.backend.document.base.DocumentStatus;
import com.ahmadabbas.filetracking.backend.document.base.payload.DocumentDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * DTO for {@link com.ahmadabbas.filetracking.backend.document.petition.PetitionDocument}
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PetitionDocumentDto extends DocumentDto implements Serializable {
    String email;
    String subject;
    DocumentStatus.ApprovalStatus approvalStatus;
}