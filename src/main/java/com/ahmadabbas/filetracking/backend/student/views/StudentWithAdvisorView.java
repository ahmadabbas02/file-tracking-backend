package com.ahmadabbas.filetracking.backend.student.views;

import com.ahmadabbas.filetracking.backend.advisor.views.AdvisorView;
import com.ahmadabbas.filetracking.backend.document.base.DocumentStatus;
import com.ahmadabbas.filetracking.backend.student.Student;
import com.blazebit.persistence.view.EntityView;
import com.blazebit.persistence.view.IdMapping;
import com.blazebit.persistence.view.Mapping;

@EntityView(Student.class)
public interface StudentWithAdvisorView {
    @IdMapping
    String getId();

    String getProgram();

    Short getYear();

    @Mapping("user.firstName")
    String getFirstName();

    @Mapping("user.lastName")
    String getLastName();

    @Mapping("CONCAT(user.firstName, ' ' ,user.lastName)")
    String getFullName();

    @Mapping("user.email")
    String getEmail();

    @Mapping("user.phoneNumber")
    String getPhoneNumber();

    AdvisorView getAdvisor();

    DocumentStatus.InternshipCompletionStatus getInternshipCompletionStatus();

    DocumentStatus.InternshipPaymentStatus getPaymentStatus();
}