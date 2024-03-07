package com.ahmadabbas.filetracking.backend.document.petition;

import com.ahmadabbas.filetracking.backend.document.base.Document;
import com.ahmadabbas.filetracking.backend.document.petition.payload.PetitionDocumentDto;
import com.ahmadabbas.filetracking.backend.document.petition.payload.PetitionDocumentMapper;
import jakarta.persistence.Entity;
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
    private boolean isApproved;

    @Override
    public PetitionDocumentDto toDto() {
        return PetitionDocumentMapper.INSTANCE.toDto(this);
    }
}
