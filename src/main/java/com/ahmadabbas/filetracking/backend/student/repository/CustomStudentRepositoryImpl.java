package com.ahmadabbas.filetracking.backend.student.repository;

import com.ahmadabbas.filetracking.backend.student.Student;
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

@RequiredArgsConstructor
public class CustomStudentRepositoryImpl implements CustomStudentRepository {
    @PersistenceContext
    private final EntityManager entityManager;
    private final CriteriaBuilderFactory criteriaBuilderFactory;

    @Override
    public Page<Student> getAllStudents(String searchQuery, Long advisorUserId, Pageable pageable) {
        PaginatedCriteriaBuilder<Student> criteriaBuilder = criteriaBuilderFactory
                .create(entityManager, Student.class)
                .fetch("advisor.user", "user", "user.roles", "user.advisor", "user.student")
                .where("advisor.user.id").eq(advisorUserId)
                .page(
                        (int) pageable.getOffset(),
                        pageable.getPageSize()
                );
        if (!searchQuery.isEmpty()) {
            if (StringUtils.isNumeric(searchQuery)) {
                searchQuery = searchQuery + "%";
                criteriaBuilder.where("id").like().value(searchQuery);
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

    private <T> Page<T> getOrderedPage(PaginatedCriteriaBuilder<T> criteriaBuilder,
                                       Pageable pageable) {
        PagingUtils.applySorting(pageable, criteriaBuilder);
        PagedList<T> resultList = criteriaBuilder.getResultList();
        return new PageImpl<>(resultList, pageable, resultList.getTotalSize());
    }
}
