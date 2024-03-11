package com.ahmadabbas.filetracking.backend.token;

import com.ahmadabbas.filetracking.backend.user.User;
import jakarta.persistence.*;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "token_id_generator")
    @SequenceGenerator(name = "token_id_generator", sequenceName = "token_id_seq", allocationSize = 1)
    private Long id;

    @Column(nullable = false, unique = true, length = 500)
    private String token;

    private boolean expired;
    private boolean blocked;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public static class TokenBuilder {
        public TokenBuilder user(final User user) {
            this.user = user;
            user.getTokens().add(this.build());
            return this;
        }
    }

}
