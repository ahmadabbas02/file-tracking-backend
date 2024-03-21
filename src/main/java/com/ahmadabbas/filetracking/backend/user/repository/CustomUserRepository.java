package com.ahmadabbas.filetracking.backend.user.repository;

import com.ahmadabbas.filetracking.backend.user.Role;
import com.ahmadabbas.filetracking.backend.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface CustomUserRepository {

    Optional<User> findUserById(Long id);

    Page<User> findAll(String name,
                       List<Role> roles,
                       Pageable pageable);

    Page<User> findAllAdvisor(String advisorId,
                              Pageable pageable);

    Page<User> findAllStudent(String studentId,
                              Pageable pageable);

}