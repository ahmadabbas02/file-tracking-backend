package com.ahmadabbas.filetracking.backend.document.internship;

import com.ahmadabbas.filetracking.backend.document.base.Document;
import com.ahmadabbas.filetracking.backend.document.internship.payload.InternshipDocumentDto;
import com.ahmadabbas.filetracking.backend.document.internship.payload.InternshipDocumentMapper;
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
public class InternshipDocument extends Document {
    @Builder.Default
    @Column(columnDefinition = "integer default 0")
    private int numberOfWorkingDays = 0;


    @Override
    public InternshipDocumentDto toDto() {
        return InternshipDocumentMapper.INSTANCE.toDto(this);
    }
}