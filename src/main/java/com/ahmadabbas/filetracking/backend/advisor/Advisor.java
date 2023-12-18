package com.ahmadabbas.filetracking.backend.advisor;

import com.ahmadabbas.filetracking.backend.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import java.util.Date;
import java.util.Objects;
import java.util.StringJoiner;

@Setter
@Getter
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
@Entity
public class Advisor {
    @Id
    @GenericGenerator(name = "advisor_id", type = com.ahmadabbas.filetracking.backend.util.AdvisorIdGenerator.class)
    @GeneratedValue(generator = "advisor_id")
    private String id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private User user;

    @CreationTimestamp
    private Date createdAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Advisor advisor)) return false;
        return Objects.equals(id, advisor.id) && Objects.equals(user, advisor.user)
                && Objects.equals(createdAt, advisor.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, user, createdAt);
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
