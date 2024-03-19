package com.ahmadabbas.filetracking.backend.student.repository;

import com.ahmadabbas.filetracking.backend.student.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomStudentRepository {
    Page<Student> getAllStudents(String searchQuery, Long advisorUserId, Pageable pageable);
}
