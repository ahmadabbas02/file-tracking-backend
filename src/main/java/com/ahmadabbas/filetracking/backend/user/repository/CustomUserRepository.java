package com.ahmadabbas.filetracking.backend.user.repository;

import com.ahmadabbas.filetracking.backend.user.*;
import org.springframework.data.domain.*;

import java.util.List;

public interface CustomUserRepository {

    Page<User> findAll(Pageable pageable);

    Page<User> findAllByNameContains(String name,
                                     Pageable pageable);

    Page<User> findAllByRoles(List<Role> roles,
                              Pageable pageable);

    Page<User> findAllByNameAndRoles(String name,
                                     List<Role> roles,
                                     Pageable pageable);
}