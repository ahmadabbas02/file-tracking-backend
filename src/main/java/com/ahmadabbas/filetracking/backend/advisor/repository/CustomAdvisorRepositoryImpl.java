package com.ahmadabbas.filetracking.backend.advisor.repository;

import com.ahmadabbas.filetracking.backend.advisor.Advisor;
import com.ahmadabbas.filetracking.backend.util.PagingUtils;
import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.PagedList;
import com.blazebit.persistence.PaginatedCriteriaBuilder;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

@RequiredArgsConstructor
public class CustomAdvisorRepositoryImpl implements CustomAdvisorRepository {

    @PersistenceContext
    private final EntityManager entityManager;
    private final CriteriaBuilderFactory criteriaBuilderFactory;

    @Override
    public Page<Advisor> findAllAdvisors(String searchQuery, Pageable pageable) {
        PaginatedCriteriaBuilder<Advisor> criteriaBuilder = criteriaBuilderFactory
                .create(entityManager, Advisor.class)
                .fetch("user", "user.roles", "user.advisor", "user.student")
                .page(
                        (int) pageable.getOffset(),
                        pageable.getPageSize()
                );
        if (!searchQuery.isBlank()) {
            if (StringUtils.isNumeric(searchQuery.replace("AP", ""))) {
                searchQuery = searchQuery + "%";
                criteriaBuilder.where("id").like(false).value(searchQuery).noEscape();
            } else {
                searchQuery = "%" + searchQuery + "%";
                criteriaBuilder.whereOr()
                        .where("user.firstName").like(false).value(searchQuery).noEscape()
                        .where("user.lastName").like(false).value(searchQuery).noEscape()
                        .endOr();
            }
        }
        return getOrderedPage(criteriaBuilder, pageable);
    }

    @Override
    public Optional<Advisor> findByUserId(Long id) {
        Advisor advisor = criteriaBuilderFactory
                .create(entityManager, Advisor.class)
                .fetch("user", "user.roles")
                .where("user.id").eq(id)
                .getSingleResult();
        return Optional.ofNullable(advisor);
    }

    private <T> Page<T> getOrderedPage(PaginatedCriteriaBuilder<T> criteriaBuilder,
                                       Pageable pageable) {
        PagingUtils.applySorting(pageable, criteriaBuilder);
        PagedList<T> resultList = criteriaBuilder.getResultList();
        return new PageImpl<>(resultList, pageable, resultList.getTotalSize());
    }
}
