package com.ahmadabbas.filetracking.backend.document.contact;

import com.ahmadabbas.filetracking.backend.document.base.Document;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@Entity
public class ContactDocument extends Document {
    private String email;
    private String phoneNumber;
    private String emergencyName;
    private String emergencyPhoneNumber;
}
