package com.ahmadabbas.filetracking.backend.auth.token;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {
    @Query("select t from Token t where t.token = :token")
    Optional<Token> findByToken(String token);

    @Query(
            """
                    select t from Token t
                    inner join User u
                    on t.user.id = u.id
                    where u.id = :id and (t.expired = false and t.blocked = false)
                    """
    )
    List<Token> findAllTokensForUserId(Long id);

}