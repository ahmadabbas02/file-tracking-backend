package com.ahmadabbas.filetracking.backend.student;

import com.ahmadabbas.filetracking.backend.advisor.Advisor;
import com.ahmadabbas.filetracking.backend.document.base.Document;
import com.ahmadabbas.filetracking.backend.user.User;
import com.ahmadabbas.filetracking.backend.util.generator.StudentIdGenerator;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.StringJoiner;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
@Entity
@NamedEntityGraph(
        name = "Student.eagerlyFetchUser",
        attributeNodes = @NamedAttributeNode("user")
)
public class Student {
    @Id
    @GenericGenerator(name = "student_id", type = StudentIdGenerator.class)
    @GeneratedValue(generator = "student_id")
    private String id;

    @Column(nullable = false)
    private String department;

    @Column(nullable = false)
    private Short year;

    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "advisor_id")
    private Advisor advisor;

    @OneToMany(mappedBy = "student")
    private Set<Document> documents;

    @CreationTimestamp
    private LocalDateTime createdAt;

    public void addDocument(Document document) {
        if (this.documents == null) {
            this.documents = new HashSet<>();
        }
        this.documents.add(document);
    }

    @JsonBackReference
    public Set<Document> getDocuments() {
        return documents;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Student.class.getSimpleName() + "[", "]")
                .add("id='" + id + "'")
                .add("department='" + department + "'")
                .add("year=" + year)
                .add("user=" + user)
                .add("advisor=" + advisor)
                .add("createdAt=" + createdAt)
                .toString();
    }
}