package com.ahmadabbas.filetracking.backend.document.contact.payload;

import com.ahmadabbas.filetracking.backend.document.base.payload.DocumentDto;
import com.ahmadabbas.filetracking.backend.document.contact.ContactDocument;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * DTO for {@link ContactDocument}
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ContactDocumentDto extends DocumentDto implements Serializable {
    String email;
    String phoneNumber;
    String emergencyName;
    String emergencyPhoneNumber;
}