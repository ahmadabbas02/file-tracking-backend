package com.ahmadabbas.filetracking.backend.student;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface StudentDao {

    Student save(Student student);

    List<Student> saveAll(List<Student> students);

    Student getStudent(String id);

    Student getStudentByUserId(Long userId);

    Page<Student> getAllStudents(Pageable pageable);

    Page<Student> getAllStudentsById(boolean contains, String id, Pageable pageable);

    Page<Student> getAllStudentsByName(boolean contains, String name, Pageable pageable);

    Page<Student> getAllStudentsByIdAndAdvisorUserId(boolean contains, String id, Long userId, Pageable pageable);

    Page<Student> getAllStudentsByNameAndAdvisorUserId(boolean contains, String name, Long userId, Pageable pageable);

    Page<Student> getAllStudentsByAdvisorUserId(Long userId, Pageable pageable);

    List<String> getAllStudentIdsByAdvisorUserId(Long userId);

    Page<Student> getAllStudentsByAdvisorId(String advisorId, Pageable pageable);

    Page<Student> getAllStudentsByNameAndAdvisorId(String name, String advisorId, Pageable pageable);

    Page<Student> getAllStudentsByIdAndAdvisorId(String studentId, String advisorId, Pageable pageable);
}
