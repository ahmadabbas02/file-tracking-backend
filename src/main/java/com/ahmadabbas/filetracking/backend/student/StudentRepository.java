package com.ahmadabbas.filetracking.backend.student;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;

import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, String> {

    @Override
    @Query("select s from Student s")
    @EntityGraph(value = "Student.eagerlyFetchUser")
    Page<Student> findAll(@NonNull Pageable pageable);

    @Query("""
            select s from Student s
            inner join User u
            on u.id = s.user.id
            where u.id = :userId
            """)
    Optional<Student> findByUserId(String userId);

    @Query("select s from Student s where s.advisor.user.id = ?1")
    @EntityGraph(value = "Student.eagerlyFetchUser")
    Page<Student> findAllByAdvisorUserId(Long userId,
                                         Pageable pageable);

    @Query("select s from Student s where upper(s.id) like upper(concat(?1, '%'))")
    @EntityGraph(value = "Student.eagerlyFetchUser")
    Page<Student> findAllByIdStartsWith(@NonNull String id,
                                        Pageable pageable);

    @Query("select s from Student s where upper(s.id) like upper(concat('%', ?1, '%'))")
    @EntityGraph(value = "Student.eagerlyFetchUser")
    Page<Student> findAllByIdContains(@NonNull String id,
                                      Pageable pageable);

    @Query("select s from Student s where upper(s.id) like upper(concat(?1, '%')) and s.advisor.user.id = ?2")
    @EntityGraph(value = "Student.eagerlyFetchUser")
    Page<Student> findAllByIdStartsWithAndAdvisor(@NonNull String id,
                                                  Long userId,
                                                  Pageable pageable);

    @Query("select s from Student s where upper(s.id) like upper(concat('%', ?1, '%')) and s.advisor.user.id = ?2")
    @EntityGraph(value = "Student.eagerlyFetchUser")
    Page<Student> findAllByIdContainsAndAdvisor(@NonNull String id,
                                                Long userId,
                                                Pageable pageable);

    @Query("select s from Student s where upper(s.user.name) like upper(concat(?1, '%'))")
    @EntityGraph(value = "Student.eagerlyFetchUser")
    Page<Student> findAllByNameStartsWith(@NonNull String name,
                                          Pageable pageable);

    @Query("select s from Student s where upper(s.user.name) like upper(concat('%', ?1, '%'))")
    @EntityGraph(value = "Student.eagerlyFetchUser")
    Page<Student> findAllByNameContains(@NonNull String name, Pageable pageable);

    @Query("select s from Student s where upper(s.user.name) like upper(concat(?1, '%')) and s.advisor.user.id = ?2")
    @EntityGraph(value = "Student.eagerlyFetchUser")
    Page<Student> findAllByNameStartsWithAndAdvisor(@NonNull String name,
                                                    Long userId,
                                                    Pageable pageable);

    @Query("select s from Student s where upper(s.user.name) like upper(concat('%', ?1, '%')) and s.advisor.user.id = ?2")
    @EntityGraph(value = "Student.eagerlyFetchUser")
    Page<Student> findAllByNameContainsAndAdvisor(@NonNull String name,
                                                  Long userId,
                                                  Pageable pageable);

}
