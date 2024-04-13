package com.ahmadabbas.filetracking.backend.advisor.repository;

import com.ahmadabbas.filetracking.backend.advisor.view.AdvisorUserView;
import com.blazebit.persistence.spring.data.repository.EntityViewRepository;

import java.util.Optional;

public interface AdvisorUserViewRepository extends EntityViewRepository<AdvisorUserView, String> {
    Optional<AdvisorUserView> findById(String advisorId);

    Optional<AdvisorUserView> findByUserId(Long userId);
}