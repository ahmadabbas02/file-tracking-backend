package com.ahmadabbas.filetracking.backend.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    @Query("select u from User u where u.role != 'CHAIR' and u.role != 'VICE_CHAIR'")
    List<User> findAllNonAdminUsers();

    boolean existsByEmail(String email);

    boolean existsByName(String name);
}
