package com.ahmadabbas.filetracking.backend.advisor;

import com.ahmadabbas.filetracking.backend.user.User;
import com.ahmadabbas.filetracking.backend.util.generator.AdvisorIdGenerator;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.StringJoiner;

@Setter
@Getter
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
@Entity
@NamedEntityGraph(
        name = "Advisor.eagerlyFetchUser",
        attributeNodes = @NamedAttributeNode("user")
)
public class Advisor {
    @Id
    @GenericGenerator(name = "advisor_id", type = AdvisorIdGenerator.class)
    @GeneratedValue(generator = "advisor_id")
    private String id;

    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "user_id")
    private User user;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(nullable = false)
    @Builder.Default
    @Version
    private Integer version = 0;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Advisor advisor)) return false;
        return Objects.equals(getId(), advisor.getId()) && Objects.equals(getUser(), advisor.getUser()) && Objects.equals(getCreatedAt(), advisor.getCreatedAt());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getUser(), getCreatedAt());
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Advisor.class.getSimpleName() + "[", "]")
                .add("id='" + id + "'")
                .add("user=" + user)
                .add("createdAt=" + createdAt)
                .toString();
    }

}
