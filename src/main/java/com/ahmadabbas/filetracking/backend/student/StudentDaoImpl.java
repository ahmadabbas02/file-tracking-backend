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
    public Student getStudentByUserId(Long userId) {
        return studentRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "student related with user id %s not found".formatted(userId)
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
    public Page<Student> getAllStudentsByIdAndAdvisorUserId(boolean contains, String id, Long userId, Pageable pageable) {
        if (contains) {
            return studentRepository.findAllByIdContainsAndAdvisorUserId(id, userId, pageable);
        }
        return studentRepository.findAllByIdStartsWithAndAdvisorUserId(id, userId, pageable);
    }

    @Override
    public Page<Student> getAllStudentsByNameAndAdvisorUserId(boolean contains, String name, Long userId, Pageable pageable) {
        if (contains) {
            return studentRepository.findAllByNameContainsAndAdvisorUserId(name, userId, pageable);
        }
        return studentRepository.findAllByNameStartsWithAndAdvisorUserId(name, userId, pageable);
    }

    @Override
    public Page<Student> getAllStudentsByAdvisorUserId(Long userId, Pageable pageable) {
        return studentRepository.findAllByAdvisorUserId(userId, pageable);
    }

    @Override
    public List<String> getAllStudentIdsByAdvisorUserId(Long userId) {
        return studentRepository.findAllByAdvisorUserId(userId);
    }

    @Override
    public Page<Student> getAllStudentsByAdvisorId(String advisorId, Pageable pageable) {
        return studentRepository.findAllByAdvisorId(advisorId, pageable);
    }

    @Override
    public Page<Student> getAllStudentsByNameAndAdvisorId(String name, String advisorId, Pageable pageable) {
        return studentRepository.findAllByNameContainsAndAdvisorId(name, advisorId, pageable);
    }

    @Override
    public Page<Student> getAllStudentsByIdAndAdvisorId(String studentId, String advisorId, Pageable pageable) {
        return studentRepository.findAllByIdStartsWithAndAdvisorId(studentId, advisorId, pageable);
    }
}
