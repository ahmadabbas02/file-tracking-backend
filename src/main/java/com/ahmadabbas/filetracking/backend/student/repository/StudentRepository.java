package com.ahmadabbas.filetracking.backend.student.repository;

import com.ahmadabbas.filetracking.backend.student.Student;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, String>, CustomStudentRepository {

    @EntityGraph(value = "Student.eagerlyFetchUser")
    @Lock(LockModeType.OPTIMISTIC)
    @Query("select s from Student s where s.id = :id")
    Optional<Student> lockStudentById(String id);

    @Override
    @Query("""
            select s from Student s
            """)
    @EntityGraph(value = "Student.eagerlyFetchUser")
    @NonNull
    Page<Student> findAll(@NonNull Pageable pageable);

    @Query("""
            select s from Student s
            inner join User u
            on u.id = s.user.id
            where u.id = :userId
            """)
    Optional<Student> findByUserId(Long userId);

    @Query("select s.id from Student s where s.advisor.user.id = ?1")
    @EntityGraph(value = "Student.eagerlyFetchUser")
    List<String> findAllByAdvisorUserId(Long userId);

    @Query("select (count(s) > 0) from Student s where upper(s.id) = upper(?1)")
    boolean existsById(@NonNull String studentId);

}
