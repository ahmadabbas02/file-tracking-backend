package com.ahmadabbas.filetracking.backend.advisor;

import com.ahmadabbas.filetracking.backend.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import java.util.Date;

@Getter
@Setter
@ToString
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
}
