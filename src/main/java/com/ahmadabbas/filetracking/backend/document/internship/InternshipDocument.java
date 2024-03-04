package com.ahmadabbas.filetracking.backend.document.internship;

import com.ahmadabbas.filetracking.backend.document.base.Document;
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
public class InternshipDocument extends Document {
    private int numberOfWorkingDays;
}