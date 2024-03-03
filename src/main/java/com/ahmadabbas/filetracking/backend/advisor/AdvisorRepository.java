package com.ahmadabbas.filetracking.backend.advisor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;

import java.util.Optional;

public interface AdvisorRepository extends JpaRepository<Advisor, String> {

    @EntityGraph(value = "Advisor.eagerlyFetchUser")
    @Override
    Page<Advisor> findAll(@NonNull Pageable pageable);

    @Query("""
            select a from Advisor a
            where a.user.id = :id
            """)
    @EntityGraph(value = "Advisor.eagerlyFetchUser")
    Optional<Advisor> findByUserId(Long id);


}
