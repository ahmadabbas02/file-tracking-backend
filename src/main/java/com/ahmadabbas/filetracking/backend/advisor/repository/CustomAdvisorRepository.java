package com.ahmadabbas.filetracking.backend.advisor.repository;

import com.ahmadabbas.filetracking.backend.advisor.view.AdvisorUserView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomAdvisorRepository {

    Page<AdvisorUserView> findAllAdvisorsProjection(String searchQuery, Pageable pageable);

}
