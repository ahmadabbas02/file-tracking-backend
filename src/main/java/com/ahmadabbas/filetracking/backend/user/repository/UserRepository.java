package com.ahmadabbas.filetracking.backend.user.repository;

import com.ahmadabbas.filetracking.backend.user.User;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, CustomUserRepository {

    @EntityGraph(attributePaths = {"advisor", "student", "roles"})
    @Query("select u from User u where u.id = :id")
    @Lock(LockModeType.OPTIMISTIC)
    Optional<User> findByIdLocked(Long id);

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query("""
            select (count(u) > 0) from User u
            where upper(u.firstName) = upper(?1) and upper(u.lastName) = upper(?2)
            """)
    boolean existsByName(String firstName, String lastName);
}
