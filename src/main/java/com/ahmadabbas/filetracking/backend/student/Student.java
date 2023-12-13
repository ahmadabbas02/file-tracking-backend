package com.ahmadabbas.filetracking.backend.student;

import com.ahmadabbas.filetracking.backend.advisor.Advisor;
import com.ahmadabbas.filetracking.backend.document.base.Document;
import com.ahmadabbas.filetracking.backend.user.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import java.util.Date;
import java.util.Set;
import java.util.StringJoiner;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
@Entity
public class Student {
    @Id
    @GenericGenerator(name = "student_id", type = com.ahmadabbas.filetracking.backend.util.StudentIdGenerator.class)
    @GeneratedValue(generator = "student_id")
    private String id;

    @Column(nullable = false)
    private String department;

    @Column(nullable = false)
    private Short year;

    @Column(nullable = false)
    private String picture;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne()
    @JoinColumn(name = "advisor_id", nullable = false)
    private Advisor advisor;

    @OneToMany(mappedBy = "student")
    private Set<Document> documents;

    @CreationTimestamp
    private Date createdAt;

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
                .add("picture='" + picture + "'")
                .add("user=" + user)
                .add("advisor=" + advisor)
                .add("documents=" + documents)
                .add("createdAt=" + createdAt)
                .toString();
    }
}