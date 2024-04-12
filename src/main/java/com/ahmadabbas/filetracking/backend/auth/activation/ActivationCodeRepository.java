package com.ahmadabbas.filetracking.backend.auth.activation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Optional;

public interface ActivationCodeRepository extends JpaRepository<ActivationCode, Integer> {

    @Query("""
            select (count(o) > 0) from ActivationCode o
            join fetch User u
            on u.id = o.user.id
            where o.expiresAt > :now and u.email = :email
            """)
    boolean existsByEmailNotExpired(String email, LocalDateTime now);

    @Query("""
            select o from ActivationCode o
            join fetch User u
            on u.id = o.user.id
            where o.code = :activationCode and o.expiresAt > :now and u.email = :email
            """)
    Optional<ActivationCode> findByCodeAndEmailNotExpired(String activationCode, String email, LocalDateTime now);

}