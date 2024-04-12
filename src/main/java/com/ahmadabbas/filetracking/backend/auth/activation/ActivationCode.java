package com.ahmadabbas.filetracking.backend.auth.activation;

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
@Table(name = "activation_code")
public class ActivationCode {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "activation_code_generator")
    @SequenceGenerator(name = "activation_code_generator", sequenceName = "activation_code_id_seq", allocationSize = 1)
    private Integer id;
    @Column(length = 12) // future proofing
    private String code;
    @CreationTimestamp
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
