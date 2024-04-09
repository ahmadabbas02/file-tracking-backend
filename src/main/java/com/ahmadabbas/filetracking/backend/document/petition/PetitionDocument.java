package com.ahmadabbas.filetracking.backend.document.petition;

import com.ahmadabbas.filetracking.backend.document.base.Document;
import com.ahmadabbas.filetracking.backend.document.petition.payload.PetitionDocumentDto;
import com.ahmadabbas.filetracking.backend.document.petition.payload.PetitionDocumentMapper;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@Entity
public class PetitionDocument extends Document {

    private String subject;
    private String email;

    @Column(columnDefinition = "boolean default false")
    @Builder.Default
    private boolean isApproved = false;

    @Override
    public PetitionDocumentDto toDto() {
        return PetitionDocumentMapper.INSTANCE.toDto(this);
    }
}
