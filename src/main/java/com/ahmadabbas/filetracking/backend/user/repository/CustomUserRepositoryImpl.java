package com.ahmadabbas.filetracking.backend.user.repository;

import com.ahmadabbas.filetracking.backend.advisor.Advisor;
import com.ahmadabbas.filetracking.backend.student.Student;
import com.ahmadabbas.filetracking.backend.user.Role;
import com.ahmadabbas.filetracking.backend.user.User;
import com.ahmadabbas.filetracking.backend.util.PagingUtils;
import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.PagedList;
import com.blazebit.persistence.PaginatedCriteriaBuilder;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

@RequiredArgsConstructor
public class CustomUserRepositoryImpl implements CustomUserRepository {
    @PersistenceContext
    private final EntityManager entityManager;
    private final CriteriaBuilderFactory criteriaBuilderFactory;

    @Override
    public Page<User> findAll(Pageable pageable) {
        PaginatedCriteriaBuilder<User> criteriaBuilder = criteriaBuilderFactory
                .create(entityManager, User.class)
                .fetch("roles", "advisor", "student")
                .page(
                        (int) pageable.getOffset(),
                        pageable.getPageSize()
                );
        return getOrderedPage(criteriaBuilder, pageable);
    }

    @Override
    public Page<User> findAll(String name, List<Role> roles, Pageable pageable) {
        PaginatedCriteriaBuilder<User> criteriaBuilder = criteriaBuilderFactory
                .create(entityManager, User.class)
                .fetch("roles")
                .page(
                        (int) pageable.getOffset(),
                        pageable.getPageSize()
                );
        if (!roles.isEmpty()) {
            criteriaBuilder.where("roles").in(roles);
        }
        if (!name.isBlank()) {
            name = "%" + name + "%";
            criteriaBuilder.whereOr()
                    .where("name.firstName").like(false).value(name).noEscape()
                    .where("name.lastName").like(false).value(name).noEscape()
                    .endOr();
        }
        return getOrderedPage(criteriaBuilder, pageable);
    }

    @Override
    public Page<User> findAllAdvisor(String advisorId, Pageable pageable) {
        advisorId = advisorId + "%";
        PaginatedCriteriaBuilder<Advisor> criteriaBuilder = criteriaBuilderFactory
                .create(entityManager, Advisor.class)
                .fetch("user", "user.roles")
                .where("id").like(false).value(advisorId).noEscape()
                .page(
                        (int) pageable.getOffset(),
                        pageable.getPageSize()
                );
        Page<Advisor> orderedAdvisorPage = getOrderedPage(criteriaBuilder, pageable);
        List<User> users = orderedAdvisorPage.getContent().stream().map(Advisor::getUser).toList();
        return new PageImpl<>(users, pageable, orderedAdvisorPage.getTotalElements());
    }

    @Override
    public Page<User> findAllStudent(String studentId, Pageable pageable) {
        studentId = studentId + "%";
        PaginatedCriteriaBuilder<Student> criteriaBuilder = criteriaBuilderFactory
                .create(entityManager, Student.class)
                .fetch("user", "user.roles")
                .where("id").like(false).value(studentId).noEscape()
                .page(
                        (int) pageable.getOffset(),
                        pageable.getPageSize()
                );
        Page<Student> orderedPage = getOrderedPage(criteriaBuilder, pageable);
        List<User> users = orderedPage.getContent().stream().map(Student::getUser).toList();
        return new PageImpl<>(users, pageable, orderedPage.getTotalElements());
    }

    private <T> Page<T> getOrderedPage(PaginatedCriteriaBuilder<T> criteriaBuilder,
                                       Pageable pageable) {
        PagingUtils.applySorting(pageable, criteriaBuilder);
        PagedList<T> resultList = criteriaBuilder.getResultList();
        return new PageImpl<>(resultList, pageable, resultList.getTotalSize());
    }
}
