package com.ahmadabbas.filetracking.backend.document.contact.payload;

import com.ahmadabbas.filetracking.backend.document.base.payload.DocumentDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
public class ContactDocumentDto extends DocumentDto implements Serializable {
    private String email;
    private String phoneNumber;
    private String homeNumber;
    private String emergencyName;
    private String emergencyPhoneNumber;
}