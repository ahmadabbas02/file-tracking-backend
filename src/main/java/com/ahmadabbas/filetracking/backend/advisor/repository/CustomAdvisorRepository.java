package com.ahmadabbas.filetracking.backend.advisor.repository;

import com.ahmadabbas.filetracking.backend.advisor.Advisor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface CustomAdvisorRepository {

    Page<Advisor> findAllAdvisors(String searchQuery,Pageable pageable);
    Optional<Advisor> findByUserId(Long id);

}
