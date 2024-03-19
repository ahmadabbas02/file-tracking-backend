package com.ahmadabbas.filetracking.backend.document.base;

import com.ahmadabbas.filetracking.backend.category.Category;
import com.ahmadabbas.filetracking.backend.document.base.payload.DocumentDto;
import com.ahmadabbas.filetracking.backend.document.base.payload.DocumentMapper;
import com.ahmadabbas.filetracking.backend.student.Student;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.*;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;


@Setter
@Getter
@AllArgsConstructor
@RequiredArgsConstructor
@SuperBuilder
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@SQLDelete(sql = "UPDATE document SET deleted = true WHERE id=?")
@FilterDef(name = "deletedDocumentFilter", parameters = @ParamDef(name = "isDeleted", type = Boolean.class))
@Filter(name = "deletedDocumentFilter", condition = "deleted = :isDeleted")
public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    @Column(name = "id", nullable = false)
    private UUID id;

    private String title;
    @Builder.Default
    private String description = "";

    @Column(nullable = false)
    private String path;

    @ManyToOne(fetch = FetchType.LAZY)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @CreationTimestamp
    private LocalDateTime uploadedAt;

    @Builder.Default
    private boolean deleted = Boolean.FALSE;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Document document)) return false;
        return Objects.equals(getId(), document.getId()) && Objects.equals(getTitle(), document.getTitle()) && Objects.equals(getDescription(), document.getDescription()) && Objects.equals(getPath(), document.getPath()) && Objects.equals(getCategory(), document.getCategory()) && Objects.equals(getStudent(), document.getStudent()) && Objects.equals(getUploadedAt(), document.getUploadedAt());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getTitle(), getDescription(), getPath(), getCategory(), getStudent(),
                getUploadedAt());
    }

    @JsonManagedReference
    public Student getStudent() {
        return student;
    }

    public DocumentDto toDto() {
        return DocumentMapper.INSTANCE.toDto(this);
    }
}
