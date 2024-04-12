package com.ahmadabbas.filetracking.backend.student.repository;

import com.ahmadabbas.filetracking.backend.document.base.DocumentStatus;
import com.ahmadabbas.filetracking.backend.student.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CustomStudentRepository {
    Page<Student> getAllStudents(String searchQuery,
                                 String advisorId,
                                 List<String> programs,
                                 List<DocumentStatus.InternshipCompletionStatus> completionStatuses,
                                 Pageable pageable);
}
