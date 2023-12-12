package com.ahmadabbas.filetracking.backend.document.base;

import com.ahmadabbas.filetracking.backend.document.category.Category;
import com.ahmadabbas.filetracking.backend.student.Student;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
@Entity
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
}
