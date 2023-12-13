package com.ahmadabbas.filetracking.backend.document.base;

import com.ahmadabbas.filetracking.backend.document.category.Category;
import com.ahmadabbas.filetracking.backend.student.Student;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;
import java.util.Objects;


@Setter
@Getter
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    private String title;

    private String path;

    @OneToOne
    private Category category;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @CreationTimestamp
    private Date uploadedAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Document document)) return false;
        return Objects.equals(id, document.id) && Objects.equals(title, document.title)
                && Objects.equals(path, document.path) && Objects.equals(category, document.category)
                && Objects.equals(student, document.student) && Objects.equals(uploadedAt, document.uploadedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, path, category, student, uploadedAt);
    }

    @JsonManagedReference
    public Student getStudent() {
        return student;
    }

}
