package com.ahmadabbas.filetracking.backend.repository;

import com.ahmadabbas.filetracking.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByLoginId(String loginId);

    Boolean existsByLoginId(String loginId);
}
