package com.ahmadabbas.filetracking.backend.repository;

import com.ahmadabbas.filetracking.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByLoginId(String loginId);

    Boolean existsByLoginId(String loginId);

    @Query("select u from User u where u.role != 'CHAIR' and u.role != 'VICE_CHAIR'")
    Optional<List<User>> findAllNonAdminUsers();
}
