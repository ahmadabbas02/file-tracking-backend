package com.ahmadabbas.filetracking.backend.advisor.repository;

import com.ahmadabbas.filetracking.backend.advisor.Advisor;
import com.blazebit.persistence.CriteriaBuilder;
import com.blazebit.persistence.CriteriaBuilderFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

import static com.ahmadabbas.filetracking.backend.util.PagingUtils.getOrderedPage;

@RequiredArgsConstructor
public class CustomAdvisorRepositoryImpl implements CustomAdvisorRepository {

    @PersistenceContext
    private final EntityManager entityManager;
    private final CriteriaBuilderFactory criteriaBuilderFactory;

    @Override
    public Page<Advisor> findAllAdvisors(String searchQuery, Pageable pageable) {
        CriteriaBuilder<Advisor> criteriaBuilder = criteriaBuilderFactory
                .create(entityManager, Advisor.class)
                .fetch("user", "user.roles", "user.advisor", "user.student");
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
        return getOrderedPage(
                criteriaBuilder.page(
                        (int) pageable.getOffset(),
                        pageable.getPageSize()
                ),
                pageable
        );
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

}
