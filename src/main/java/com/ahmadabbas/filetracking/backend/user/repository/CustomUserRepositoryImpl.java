package com.ahmadabbas.filetracking.backend.user.repository;

import com.ahmadabbas.filetracking.backend.user.*;
import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.PagedList;
import com.blazebit.persistence.PaginatedCriteriaBuilder;
import jakarta.persistence.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class CustomUserRepositoryImpl implements CustomUserRepository {
    @PersistenceContext
    private final EntityManager entityManager;
    private final CriteriaBuilderFactory criteriaBuilderFactory;

    @Override
    public Page<User> findAll(Pageable pageable) {
        PaginatedCriteriaBuilder<User> criteriaBuilder = criteriaBuilderFactory
                .create(entityManager, User.class)
                .fetch("roles")
                .page(
                        (int) pageable.getOffset(),
                        pageable.getPageSize()
                );
        return getOrderedUsersPage(pageable, criteriaBuilder);
    }

    @Override
    public Page<User> findAllByNameContains(String name, Pageable pageable) {
        name = "%" + name + "%";
        PaginatedCriteriaBuilder<User> criteriaBuilder = criteriaBuilderFactory
                .create(entityManager, User.class)
                .fetch("roles")
                .whereOr()
                .where("name.firstName").like(false).value(name).noEscape()
                .where("name.lastName").like(false).value(name).noEscape()
                .endOr()
                .page(
                        (int) pageable.getOffset(),
                        pageable.getPageSize()
                );
        return getOrderedUsersPage(pageable, criteriaBuilder);
    }

    @Override
    public Page<User> findAllByRoles(List<Role> roles, Pageable pageable) {
        PaginatedCriteriaBuilder<User> criteriaBuilder = criteriaBuilderFactory
                .create(entityManager, User.class)
                .fetch("roles")
                .where("roles").in(roles)
                .page(
                        (int) pageable.getOffset(),
                        pageable.getPageSize()
                );
        return getOrderedUsersPage(pageable, criteriaBuilder);
    }

    @Override
    public Page<User> findAllByNameAndRoles(String name, List<Role> roles, Pageable pageable) {
        name = "%" + name + "%";
        PaginatedCriteriaBuilder<User> criteriaBuilder = criteriaBuilderFactory
                .create(entityManager, User.class)
                .fetch("roles")
                .where("roles").in(roles)
                .whereOr()
                .where("name.firstName").like(false).value(name).noEscape()
                .where("name.lastName").like(false).value(name).noEscape()
                .endOr()
                .page(
                        (int) pageable.getOffset(),
                        pageable.getPageSize()
                );
        return getOrderedUsersPage(pageable, criteriaBuilder);
    }

    private Page<User> getOrderedUsersPage(Pageable pageable,
                                           PaginatedCriteriaBuilder<User> criteriaBuilder) {
        Map<String, Sort.Direction> sortProperties = pageable.getSort()
                .stream()
                .collect(Collectors.toMap(Sort.Order::getProperty, Sort.Order::getDirection));
        for (var entry : sortProperties.entrySet()) {
            criteriaBuilder.orderBy(entry.getKey(), entry.getValue().equals(Sort.Direction.ASC));
        }
        criteriaBuilder.orderBy("id", true);
        PagedList<User> resultList = criteriaBuilder.getResultList();
        return new PageImpl<>(resultList, pageable, resultList.getTotalSize());
    }
}
