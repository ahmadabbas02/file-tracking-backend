package com.ahmadabbas.filetracking.backend.auth.otp;

import com.ahmadabbas.filetracking.backend.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class Otp {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "otp_generator")
    @SequenceGenerator(name = "otp_generator", sequenceName = "otp_id_seq", allocationSize = 1)
    private Integer id;
    @Column(length = 12) // future proofing
    private String otp;
    @CreationTimestamp
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
