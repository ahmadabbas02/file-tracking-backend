package com.ahmadabbas.filetracking.backend.student.repository;

import com.ahmadabbas.filetracking.backend.student.Student;
import com.blazebit.persistence.CriteriaBuilder;
import com.blazebit.persistence.CriteriaBuilderFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;

import static com.ahmadabbas.filetracking.backend.util.PagingUtils.getOrderedPage;

@RequiredArgsConstructor
public class CustomStudentRepositoryImpl implements CustomStudentRepository {
    @PersistenceContext
    private final EntityManager entityManager;
    private final CriteriaBuilderFactory criteriaBuilderFactory;

    @Override
    public Page<Student> getAllStudents(String searchQuery, Long advisorUserId, Pageable pageable) {
        CriteriaBuilder<Student> criteriaBuilder = criteriaBuilderFactory
                .create(entityManager, Student.class)
                .fetch("advisor.user", "user", "user.roles", "user.advisor", "user.student")
                .where("advisor.user.id").eq(advisorUserId);
        if (!searchQuery.isEmpty()) {
            if (StringUtils.isNumeric(searchQuery)) {
                searchQuery = searchQuery + "%";
                criteriaBuilder.where("id").like().value(searchQuery).noEscape();
            } else {
                searchQuery = searchQuery.trim();
                String[] names = searchQuery.split("\\s+");
                System.out.println("names = " + Arrays.toString(names));
                if (names.length == 2) {
                    String firstName = "%" + names[0] + "%";
                    String lastName = "%" + names[1] + "%";
                    criteriaBuilder
                            .where("user.firstName").like(false).value(firstName).noEscape()
                            .where("user.lastName").like(false).value(lastName).noEscape();
                } else {
                    searchQuery = "%" + searchQuery + "%";
                    criteriaBuilder.whereOr()
                            .where("user.firstName").like(false).value(searchQuery).noEscape()
                            .where("user.lastName").like(false).value(searchQuery).noEscape()
                            .endOr();
                }
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

}
