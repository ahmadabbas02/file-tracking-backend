package com.ahmadabbas.filetracking.backend.student.repository;

import com.ahmadabbas.filetracking.backend.document.base.DocumentStatus;
import com.ahmadabbas.filetracking.backend.student.Student;
import com.ahmadabbas.filetracking.backend.util.SearchCriteriaUtils;
import com.blazebit.persistence.CriteriaBuilder;
import com.blazebit.persistence.CriteriaBuilderFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static com.ahmadabbas.filetracking.backend.util.PagingUtils.getOrderedPage;

@RequiredArgsConstructor
public class CustomStudentRepositoryImpl implements CustomStudentRepository {
    @PersistenceContext
    private final EntityManager entityManager;
    private final CriteriaBuilderFactory criteriaBuilderFactory;

    @Override
    public Page<Student> getAllStudents(String searchQuery,
                                        String advisorId,
                                        List<String> programs,
                                        List<DocumentStatus.InternshipCompletionStatus> completionStatuses,
                                        Pageable pageable) {
        CriteriaBuilder<Student> criteriaBuilder = criteriaBuilderFactory
                .create(entityManager, Student.class)
                .fetch("advisor.user", "user", "user.roles", "user.advisor", "user.student");
        if (!advisorId.isEmpty()) {
            criteriaBuilder.where("advisor.id").eq(advisorId);
        }
        if (!programs.isEmpty()) {
            criteriaBuilder.where("program").in(programs);
        }
        if (!completionStatuses.isEmpty()) {
            criteriaBuilder.where("internshipCompletionStatus").in(completionStatuses);
        }
        if (!searchQuery.isEmpty()) {
            if (StringUtils.isNumeric(searchQuery)) {
                searchQuery = searchQuery + "%";
                criteriaBuilder.where("id").like().value(searchQuery).noEscape();
            } else {
                SearchCriteriaUtils.addNameCriteria(
                        criteriaBuilder,
                        searchQuery
                );
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
