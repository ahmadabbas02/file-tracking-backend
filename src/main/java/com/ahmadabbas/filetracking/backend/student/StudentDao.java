package com.ahmadabbas.filetracking.backend.student;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface StudentDao {

    Student save(Student student);

    List<Student> saveAll(List<Student> students);

    Student getStudent(String id);

    Student getStudentByUserId(String userId);

    Page<Student> getAllStudents(Pageable pageable);

    Page<Student> getAllStudentsById(boolean contains, String id, Pageable pageable);

    Page<Student> getAllStudentsByName(boolean contains, String name, Pageable pageable);

    Page<Student> getAllStudentsByIdAndAdvisor(boolean contains, String id, Long userId, Pageable pageable);

    Page<Student> getAllStudentsByNameAndAdvisor(boolean contains, String name, Long userId, Pageable pageable);

    Page<Student> getAllStudentsByAdvisorUserId(Long userId, Pageable pageable);

}
