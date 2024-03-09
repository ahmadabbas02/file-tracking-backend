package com.ahmadabbas.filetracking.backend.document.contact;

import com.ahmadabbas.filetracking.backend.document.base.Document;
import com.ahmadabbas.filetracking.backend.document.contact.payload.ContactDocumentDto;
import com.ahmadabbas.filetracking.backend.document.contact.payload.ContactDocumentMapper;
import jakarta.persistence.Entity;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Objects;
import java.util.StringJoiner;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@SuperBuilder
@Entity
public class ContactDocument extends Document {
    private String email;
    private String phoneNumber;
    private String emergencyName;
    private String emergencyPhoneNumber;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ContactDocument that)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(getEmail(), that.getEmail()) && Objects.equals(getPhoneNumber(), that.getPhoneNumber()) && Objects.equals(getEmergencyName(), that.getEmergencyName()) && Objects.equals(getEmergencyPhoneNumber(), that.getEmergencyPhoneNumber());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getEmail(), getPhoneNumber(), getEmergencyName(), getEmergencyPhoneNumber());
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", ContactDocument.class.getSimpleName() + "[", "]")
                .add("email='" + email + "'")
                .add("phoneNumber='" + phoneNumber + "'")
                .add("emergencyName='" + emergencyName + "'")
                .add("emergencyPhoneNumber='" + emergencyPhoneNumber + "'")
                .toString();
    }

    @Override
    public ContactDocumentDto toDto() {
        return ContactDocumentMapper.INSTANCE.toDto(this);
    }
}
