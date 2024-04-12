package com.ahmadabbas.filetracking.backend.document.petition;

import com.ahmadabbas.filetracking.backend.document.base.Document;
import com.ahmadabbas.filetracking.backend.document.base.DocumentStatus;
import com.ahmadabbas.filetracking.backend.document.petition.payload.PetitionDocumentDto;
import com.ahmadabbas.filetracking.backend.document.petition.payload.PetitionDocumentMapper;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@Entity
public class PetitionDocument extends Document {

    private String subject;
    private String email;

    @Builder.Default
    @Enumerated(value = EnumType.STRING)
    private DocumentStatus.ApprovalStatus approvalStatus = DocumentStatus.ApprovalStatus.PENDING;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PetitionDocument that)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(getSubject(), that.getSubject())
               && Objects.equals(getEmail(), that.getEmail())
               && getApprovalStatus() == that.getApprovalStatus();
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getSubject(), getEmail(), getApprovalStatus());
    }

    @Override
    public PetitionDocumentDto toDto() {
        return PetitionDocumentMapper.INSTANCE.toDto(this);
    }
}
