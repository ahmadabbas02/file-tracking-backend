package com.ahmadabbas.filetracking.backend.advisor.repository;

import com.ahmadabbas.filetracking.backend.advisor.views.AdvisorView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface CustomAdvisorRepository {

    Optional<AdvisorView> findByAdvisorId(String advisorId);

    Optional<AdvisorView> findByUserId(Long id);

    Page<AdvisorView> findAllAdvisorsProjection(String searchQuery, Pageable pageable);

}
