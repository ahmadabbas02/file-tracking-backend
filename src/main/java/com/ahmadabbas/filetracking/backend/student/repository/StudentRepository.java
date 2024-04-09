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

    @Query("select s from Student s where s.advisor.user.id = ?1")
    @EntityGraph(value = "Student.eagerlyFetchUser")
    Page<Student> findAllByAdvisorUserId(Long userId,
                                         Pageable pageable);

    @Query("select s from Student s where s.advisor.id = ?1")
    @EntityGraph(value = "Student.eagerlyFetchUser")
    Page<Student> findAllByAdvisorId(String advisorId,
                                     Pageable pageable);

    @Query("select s.id from Student s where s.advisor.user.id = ?1")
    @EntityGraph(value = "Student.eagerlyFetchUser")
    List<String> findAllByAdvisorUserId(Long userId);

    @Query("""
            select s from Student s
            where upper(s.id) like concat(?1, '%')
            """)
    @EntityGraph(value = "Student.eagerlyFetchUser")
    Page<Student> findAllByIdStartsWith(@NonNull String id,
                                        Pageable pageable);

    @Query("""
            select s from Student s
            where upper(s.id) like upper(concat('%', ?1, '%'))
            """)
    @EntityGraph(value = "Student.eagerlyFetchUser")
    Page<Student> findAllByIdContains(@NonNull String id,
                                      Pageable pageable);

    @Query("""
            select s from Student s
            where upper(s.id) like upper(concat(?1, '%'))
            and s.advisor.user.id = ?2
            """)
    @EntityGraph(value = "Student.eagerlyFetchUser")
    Page<Student> findAllByIdStartsWithAndAdvisorUserId(@NonNull String id,
                                                        Long userId,
                                                        Pageable pageable);

    @Query("""
            select s from Student s
            where upper(s.id) like upper(concat('%', ?1, '%'))
            and s.advisor.user.id = ?2
            """)
    @EntityGraph(value = "Student.eagerlyFetchUser")
    Page<Student> findAllByIdContainsAndAdvisorUserId(@NonNull String id,
                                                      Long userId,
                                                      Pageable pageable);

    @Query("""
            select s from Student s
            where upper(s.id) like upper(concat(?1, '%'))
            and s.advisor.id = ?2
            """)
    @EntityGraph(value = "Student.eagerlyFetchUser")
    Page<Student> findAllByIdStartsWithAndAdvisorId(@NonNull String studentId,
                                                    String advisorId,
                                                    Pageable pageable);

    @Query("""
            select s from Student s
            where upper(s.user.firstName) like upper(concat(?1, '%'))
            or upper(s.user.lastName) like upper(concat(?1, '%'))
            """)
    @EntityGraph(value = "Student.eagerlyFetchUser")
    Page<Student> findAllByNameStartsWith(@NonNull String name,
                                          Pageable pageable);

    @Query("""
            select s from Student s
            where upper(s.user.firstName) like upper(concat('%', ?1, '%'))
            or upper(s.user.lastName) like upper(concat('%', ?1, '%'))""")
    @EntityGraph(value = "Student.eagerlyFetchUser")
    Page<Student> findAllByNameContains(@NonNull String name, Pageable pageable);

    @Query("""
            select s from Student s
            where (upper(s.user.firstName) like upper(concat(?1, '%'))
            or upper(s.user.lastName) like upper(concat(?1, '%')))
            and s.advisor.user.id = ?2
            """)
    @EntityGraph(value = "Student.eagerlyFetchUser")
    Page<Student> findAllByNameStartsWithAndAdvisorUserId(@NonNull String name,
                                                          Long userId,
                                                          Pageable pageable);

    @EntityGraph(attributePaths = {"advisor.user", "user", "user.roles", "user.advisor", "user.student"})
    @Query("""
            select s from Student s
            where (upper(s.user.firstName) like upper(concat('%', ?1, '%'))
            or upper(s.user.lastName) like upper(concat('%', ?1, '%')))
            and s.advisor.user.id = ?2
            """)
//    @EntityGraph(value = "Student.eagerlyFetchUser")
    Page<Student> findAllByNameContainsAndAdvisorUserId(@NonNull String name,
                                                        Long userId,
                                                        Pageable pageable);

    @Query("""
            select s from Student s
            where (upper(s.user.firstName) like upper(concat('%', ?1, '%'))
            or upper(s.user.lastName) like upper(concat('%', ?1, '%')))
            and s.advisor.id = ?2
            """)
    @EntityGraph(value = "Student.eagerlyFetchUser")
    Page<Student> findAllByNameContainsAndAdvisorId(@NonNull String name,
                                                    String advisorId,
                                                    Pageable pageable);

}
