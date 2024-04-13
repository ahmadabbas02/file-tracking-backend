package com.ahmadabbas.filetracking.backend.user.repository;

import com.ahmadabbas.filetracking.backend.advisor.Advisor;
import com.ahmadabbas.filetracking.backend.student.Student;
import com.ahmadabbas.filetracking.backend.user.Role;
import com.ahmadabbas.filetracking.backend.user.User;
import com.ahmadabbas.filetracking.backend.util.SearchCriteriaUtils;
import com.blazebit.persistence.CriteriaBuilder;
import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.PaginatedCriteriaBuilder;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static com.ahmadabbas.filetracking.backend.util.PagingUtils.getOrderedPage;

@RequiredArgsConstructor
public class CustomUserRepositoryImpl implements CustomUserRepository {
    @PersistenceContext
    private final EntityManager entityManager;
    private final CriteriaBuilderFactory criteriaBuilderFactory;

    @Override
    public Optional<User> findUserById(Long id) {
        CriteriaBuilder<User> cb = criteriaBuilderFactory
                .create(entityManager, User.class)
                .fetch("advisor", "student", "roles")
                .where("id").eq(id);
        try {
            return Optional.ofNullable(cb.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public Page<User> findAll(String name, List<Role> roles, Pageable pageable) {
        CriteriaBuilder<User> criteriaBuilder = criteriaBuilderFactory
                .create(entityManager, User.class)
                .fetch("advisor", "student", "roles");
        if (!roles.isEmpty()) {
            criteriaBuilder.where("roles").in(roles);
        }
        if (!name.isEmpty()) {
            SearchCriteriaUtils.addNameCriteria(
                    criteriaBuilder,
                    name,
                    "firstName",
                    "lastName"
            );
        }
        return getOrderedPage(
                criteriaBuilder.page(
                        (int) pageable.getOffset(),
                        pageable.getPageSize()
                ),
                pageable
        );
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

}
