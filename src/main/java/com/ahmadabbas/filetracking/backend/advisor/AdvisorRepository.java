package com.ahmadabbas.filetracking.backend.advisor;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdvisorRepository extends JpaRepository<Advisor, String> {
    Optional<Advisor> findAdvisorByUserId(Long userId);

}
