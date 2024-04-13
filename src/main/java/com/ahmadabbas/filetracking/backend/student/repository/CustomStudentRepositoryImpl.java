package com.ahmadabbas.filetracking.backend.student.repository;

import com.ahmadabbas.filetracking.backend.document.base.DocumentStatus;
import com.ahmadabbas.filetracking.backend.student.Student;
import com.ahmadabbas.filetracking.backend.student.view.StudentAdvisorView;
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

import java.util.List;
import java.util.Optional;

import static com.ahmadabbas.filetracking.backend.util.PagingUtils.getOrderedPage;

@RequiredArgsConstructor
public class CustomStudentRepositoryImpl implements CustomStudentRepository {
    @PersistenceContext
    private final EntityManager entityManager;
    private final EntityViewManager evm;
    private final CriteriaBuilderFactory criteriaBuilderFactory;

    @Override
    public Optional<StudentAdvisorView> getStudentViewById(String studentId){
        CriteriaBuilder<Student> criteriaBuilder = criteriaBuilderFactory
                .create(entityManager, Student.class);
        CriteriaBuilder<StudentAdvisorView> studentViewCriteriaBuilder
                = evm.applySetting(EntityViewSetting.create(StudentAdvisorView.class), criteriaBuilder);
        studentViewCriteriaBuilder.where("id").eq(studentId);
        try {
            return Optional.ofNullable(studentViewCriteriaBuilder.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }

    }

    @Override
    public Optional<StudentAdvisorView> getStudentViewByUserId(Long userId) {
        CriteriaBuilder<Student> criteriaBuilder = criteriaBuilderFactory
                .create(entityManager, Student.class);
        CriteriaBuilder<StudentAdvisorView> studentViewCriteriaBuilder
                = evm.applySetting(EntityViewSetting.create(StudentAdvisorView.class), criteriaBuilder);
        studentViewCriteriaBuilder.where("user.id").eq(userId);
        try {
            return Optional.ofNullable(studentViewCriteriaBuilder.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public Page<StudentAdvisorView> getAllStudents(String searchQuery,
                                                   String advisorId,
                                                   List<String> programs,
                                                   List<DocumentStatus.InternshipCompletionStatus> completionStatuses,
                                                   Pageable pageable) {
        CriteriaBuilder<Student> criteriaBuilder = criteriaBuilderFactory
                .create(entityManager, Student.class);
        CriteriaBuilder<StudentAdvisorView> studentViewCriteriaBuilder
                = evm.applySetting(EntityViewSetting.create(StudentAdvisorView.class), criteriaBuilder);
        if (!advisorId.isEmpty()) {
            studentViewCriteriaBuilder.where("advisor.id").eq(advisorId);
        }
        if (!programs.isEmpty()) {
            studentViewCriteriaBuilder.where("program").in(programs);
        }
        if (!completionStatuses.isEmpty()) {
            studentViewCriteriaBuilder.where("internshipCompletionStatus").in(completionStatuses);
        }
        if (!searchQuery.isEmpty()) {
            if (StringUtils.isNumeric(searchQuery)) {
                searchQuery = searchQuery + "%";
                studentViewCriteriaBuilder.where("id").like().value(searchQuery).noEscape();
            } else {
                SearchCriteriaUtils.addNameCriteria(
                        studentViewCriteriaBuilder,
                        searchQuery
                );
            }
        }
        return getOrderedPage(
                studentViewCriteriaBuilder.page(
                        (int) pageable.getOffset(),
                        pageable.getPageSize()
                ),
                pageable
        );
    }
}
