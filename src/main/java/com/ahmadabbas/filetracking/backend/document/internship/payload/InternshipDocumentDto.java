package com.ahmadabbas.filetracking.backend.document.internship.payload;

import com.ahmadabbas.filetracking.backend.document.base.payload.DocumentDto;
import com.ahmadabbas.filetracking.backend.document.internship.InternshipDocument;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * DTO for {@link InternshipDocument}
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class InternshipDocumentDto extends DocumentDto implements Serializable {
    int numberOfWorkingDays;
}