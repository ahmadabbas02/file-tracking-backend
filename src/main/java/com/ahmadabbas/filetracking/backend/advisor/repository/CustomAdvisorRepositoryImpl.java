package com.ahmadabbas.filetracking.backend.advisor.repository;

import com.ahmadabbas.filetracking.backend.advisor.Advisor;
import com.ahmadabbas.filetracking.backend.advisor.views.AdvisorView;
import com.ahmadabbas.filetracking.backend.util.SearchCriteriaUtils;
import com.blazebit.persistence.CriteriaBuilder;
import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.view.EntityViewManager;
import com.blazebit.persistence.view.EntityViewSetting;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
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
    private final EntityViewManager evm;
    private final CriteriaBuilderFactory criteriaBuilderFactory;

    @Override
    public Optional<AdvisorView> findByAdvisorId(String advisorId) {
        CriteriaBuilder<Advisor> cb = criteriaBuilderFactory
                .create(entityManager, Advisor.class)
                .where("id").eq(advisorId);
        CriteriaBuilder<AdvisorView> advisorViewCriteriaBuilder =
                evm.applySetting(EntityViewSetting.create(AdvisorView.class), cb);
        try {
            return Optional.ofNullable(advisorViewCriteriaBuilder.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<AdvisorView> findByUserId(Long userId) {
        CriteriaBuilder<Advisor> cb = criteriaBuilderFactory
                .create(entityManager, Advisor.class)
                .where("user.id").eq(userId);
        CriteriaBuilder<AdvisorView> advisorViewCriteriaBuilder =
                evm.applySetting(EntityViewSetting.create(AdvisorView.class), cb);
        try {
            return Optional.ofNullable(advisorViewCriteriaBuilder.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public Page<AdvisorView> findAllAdvisorsProjection(String searchQuery, Pageable pageable) {
        CriteriaBuilder<Advisor> cb = criteriaBuilderFactory
                .create(entityManager, Advisor.class);
        CriteriaBuilder<AdvisorView> advisorViewCriteriaBuilder =
                evm.applySetting(EntityViewSetting.create(AdvisorView.class), cb);
        if (!searchQuery.isBlank()) {
            if (StringUtils.isNumeric(searchQuery.replace("AP", ""))) {
                searchQuery = searchQuery + "%";
                advisorViewCriteriaBuilder
                        .where("id").like(false).value(searchQuery).noEscape();
            } else {
                SearchCriteriaUtils.addNameCriteria(
                        advisorViewCriteriaBuilder,
                        searchQuery,
                        "user.firstName",
                        "user.lastName"
                );
            }
        }
        return getOrderedPage(
                advisorViewCriteriaBuilder.page(
                        (int) pageable.getOffset(),
                        pageable.getPageSize()
                ),
                pageable
        );
    }

}
