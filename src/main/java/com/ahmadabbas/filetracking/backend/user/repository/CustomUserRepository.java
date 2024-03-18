package com.ahmadabbas.filetracking.backend.user.repository;

import com.ahmadabbas.filetracking.backend.user.Role;
import com.ahmadabbas.filetracking.backend.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CustomUserRepository {

    Page<User> findAll(Pageable pageable);

    Page<User> findAll(String name,
                       List<Role> roles,
                       Pageable pageable);

    Page<User> findAllAdvisor(String advisorId,
                              Pageable pageable);

    Page<User> findAllStudent(String studentId,
                              Pageable pageable);

}