package com.ahmadabbas.filetracking.backend.student;

import com.ahmadabbas.filetracking.backend.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class StudentDaoImpl implements StudentDao {
    private final StudentRepository studentRepository;

    @Override
    public Student save(Student student) {
        return studentRepository.save(student);
    }

    @Override
    public List<Student> saveAll(List<Student> students) {
        return studentRepository.saveAll(students);
    }

    @Override
    public Student getStudent(String id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "student with id %s not found".formatted(id)
                ));
    }

    @Override
    public Page<Student> getAllStudents(Pageable pageable) {
        return studentRepository.findAll(pageable);
    }

    @Override
    public Page<Student> getAllStudentsById(boolean contains, String id, Pageable pageable) {
        if (contains) {
            return studentRepository.findAllByIdContains(id, pageable);
        }
        return studentRepository.findAllByIdStartsWith(id, pageable);
    }

    @Override
    public Page<Student> getAllStudentsByName(boolean contains, String name, Pageable pageable) {
        if (contains) {
            return studentRepository.findAllByNameContains(name, pageable);
        }
        return studentRepository.findAllByNameStartsWith(name, pageable);
    }

    @Override
    public Page<Student> getAllStudentsByIdAndAdvisor(boolean contains, String id, Long userId, Pageable pageable) {
        if (contains) {
            return studentRepository.findAllByIdContainsAndAdvisor(id, userId, pageable);
        }
        return studentRepository.findAllByIdStartsWithAndAdvisor(id, userId, pageable);
    }

    @Override
    public Page<Student> getAllStudentsByNameAndAdvisor(boolean contains, String name, Long userId, Pageable pageable) {
        if (contains) {
            return studentRepository.findAllByNameContainsAndAdvisor(name, userId, pageable);
        }
        return studentRepository.findAllByNameStartsWithAndAdvisor(name, userId, pageable);
    }

    @Override
    public Page<Student> getAllStudentsByAdvisorUserId(Long userId, Pageable pageable) {
        return studentRepository.findAllByAdvisorUserId(userId, pageable);
    }
}
