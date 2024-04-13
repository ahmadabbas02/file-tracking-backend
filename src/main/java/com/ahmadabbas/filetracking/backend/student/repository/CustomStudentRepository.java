package com.ahmadabbas.filetracking.backend.student.repository;

import com.ahmadabbas.filetracking.backend.document.base.DocumentStatus;
import com.ahmadabbas.filetracking.backend.student.view.StudentAdvisorView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface CustomStudentRepository {
    Optional<StudentAdvisorView> getStudentViewById(String studentId);

    Optional<StudentAdvisorView> getStudentViewByUserId(Long userId);

    Page<StudentAdvisorView> getAllStudents(String searchQuery,
                                            String advisorId,
                                            List<String> programs,
                                            List<DocumentStatus.InternshipCompletionStatus> completionStatuses,
                                            Pageable pageable);
}
