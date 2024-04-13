package com.ahmadabbas.filetracking.backend.student.repository;

import com.ahmadabbas.filetracking.backend.student.view.StudentView;
import com.blazebit.persistence.spring.data.repository.EntityViewRepository;

import java.util.Optional;

public interface StudentViewRepository extends EntityViewRepository<StudentView, String> {
  Optional<StudentView> findByUserId(Long userId);
}