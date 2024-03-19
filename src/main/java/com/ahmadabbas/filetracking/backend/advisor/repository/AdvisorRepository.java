package com.ahmadabbas.filetracking.backend.advisor.repository;

import com.ahmadabbas.filetracking.backend.advisor.Advisor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdvisorRepository extends JpaRepository<Advisor, String>, CustomAdvisorRepository {

}
