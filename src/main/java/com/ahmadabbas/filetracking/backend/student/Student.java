package com.ahmadabbas.filetracking.backend.student;

import com.ahmadabbas.filetracking.backend.advisor.Advisor;
import com.ahmadabbas.filetracking.backend.document.internship.InternshipStatus;
import com.ahmadabbas.filetracking.backend.user.User;
import com.ahmadabbas.filetracking.backend.util.generator.StudentIdGenerator;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.Objects;
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
    private String program;

    @Column(nullable = false)
    private Short year;

    @OneToOne(cascade = {CascadeType.PERSIST})
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "advisor_id")
    private Advisor advisor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private InternshipStatus.CompletionStatus internshipCompletionStatus = InternshipStatus.CompletionStatus.INCOMPLETE;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private InternshipStatus.PaymentStatus paymentStatus = InternshipStatus.PaymentStatus.NOT_PAID;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @Override
    public String toString() {
        return new StringJoiner(", ", Student.class.getSimpleName() + "[", "]")
                .add("id='" + id + "'")
                .add("department='" + program + "'")
                .add("year=" + year)
                .add("createdAt=" + createdAt)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Student student)) return false;
        return Objects.equals(getId(), student.getId()) && Objects.equals(getProgram(), student.getProgram()) && Objects.equals(getYear(), student.getYear()) && Objects.equals(getUser(), student.getUser()) && Objects.equals(getAdvisor(), student.getAdvisor()) && getInternshipCompletionStatus() == student.getInternshipCompletionStatus() && getPaymentStatus() == student.getPaymentStatus() && Objects.equals(getCreatedAt(), student.getCreatedAt());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getProgram(), getYear(), getUser(), getAdvisor(), getInternshipCompletionStatus(), getPaymentStatus(), getCreatedAt());
    }
}