package com.ahmadabbas.filetracking.backend.user.repository;

import com.ahmadabbas.filetracking.backend.user.*;
import com.blazebit.persistence.CriteriaBuilderFactory;
import jakarta.persistence.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;

import java.util.List;

@RequiredArgsConstructor
public class CustomUserRepositoryImpl implements CustomUserRepository {
    @PersistenceContext
    private final EntityManager entityManager;
    private final CriteriaBuilderFactory criteriaBuilderFactory;

    @Override
    public Page<User> findAll(Pageable pageable) {
        com.blazebit.persistence.PagedList<User> resultList = criteriaBuilderFactory
                .create(entityManager, User.class)
                .fetch("roles")
                .orderBy("id", true)
                .page(
                        (int) pageable.getOffset(),
                        pageable.getPageSize()
                )
                .withCountQuery(false)
                .getResultList();
        return new PageImpl<>(resultList, pageable, resultList.getTotalPages());
    }

    @Override
    public Page<User> findAllByNameContains(String name, Pageable pageable) {
        name = "%" + name + "%";
        com.blazebit.persistence.PagedList<User> resultList = criteriaBuilderFactory
                .create(entityManager, User.class)
                .fetch("roles")
                .whereOr()
                .where("name.firstName").like(false).value(name).noEscape()
                .where("name.lastName").like(false).value(name).noEscape()
                .endOr()
                .orderBy("id", true)
                .page(
                        (int) pageable.getOffset(),
                        pageable.getPageSize()
                )
                .withCountQuery(false)
                .getResultList();
        return new PageImpl<>(resultList, pageable, resultList.getTotalPages());
    }

    @Override
    public Page<User> findAllByRoles(List<Role> roles, Pageable pageable) {
        com.blazebit.persistence.PagedList<User> resultList = criteriaBuilderFactory
                .create(entityManager, User.class)
                .fetch("roles")
                .where("roles").in(roles)
                .orderBy("id", true)
                .page(
                        (int) pageable.getOffset(),
                        pageable.getPageSize()
                )
                .withCountQuery(false)
                .getResultList();
        return new PageImpl<>(resultList, pageable, resultList.getTotalPages());
    }

    @Override
    public Page<User> findAllByNameAndRoles(String name, List<Role> roles, Pageable pageable) {
        name = "%" + name + "%";
        com.blazebit.persistence.PagedList<User> resultList = criteriaBuilderFactory
                .create(entityManager, User.class)
                .fetch("roles")
                .where("roles").in(roles)
                .whereOr()
                .where("name.firstName").like(false).value(name).noEscape()
                .where("name.lastName").like(false).value(name).noEscape()
                .endOr()
                .orderBy("id", true)
                .page(
                        (int) pageable.getOffset(),
                        pageable.getPageSize()
                )
                .withCountQuery(false)
                .getResultList();
        return new PageImpl<>(resultList, pageable, resultList.getTotalPages());
    }
}
