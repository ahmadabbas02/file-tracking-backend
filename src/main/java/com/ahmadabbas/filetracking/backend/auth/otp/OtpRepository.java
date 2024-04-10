package com.ahmadabbas.filetracking.backend.auth.otp;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Optional;

public interface OtpRepository extends JpaRepository<Otp, Integer> {

    @Query("""
            select o from Otp o
            join fetch User u
            on u.id = o.user.id
            where o.otp = :otp and o.expiresAt > :now and u.email = :email
            """)
    Optional<Otp> findByOtpAndEmail(String otp, String email, LocalDateTime now);

}