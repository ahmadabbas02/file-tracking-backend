package com.ahmadabbas.filetracking.backend.document.contact;

import com.ahmadabbas.filetracking.backend.document.base.Document;
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
        return Objects.equals(email, that.email) && Objects.equals(phoneNumber, that.phoneNumber)
                && Objects.equals(emergencyName, that.emergencyName)
                && Objects.equals(emergencyPhoneNumber, that.emergencyPhoneNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), email, phoneNumber, emergencyName, emergencyPhoneNumber);
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
}
