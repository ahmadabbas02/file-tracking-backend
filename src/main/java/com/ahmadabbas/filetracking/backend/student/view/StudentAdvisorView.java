package com.ahmadabbas.filetracking.backend.student.view;

import com.ahmadabbas.filetracking.backend.advisor.view.AdvisorUserView;
import com.ahmadabbas.filetracking.backend.document.base.DocumentStatus;
import com.ahmadabbas.filetracking.backend.student.Student;
import com.blazebit.persistence.view.EntityView;
import com.blazebit.persistence.view.IdMapping;
import com.blazebit.persistence.view.Mapping;

@EntityView(Student.class)
public interface StudentAdvisorView {
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

    @Mapping("user.picture")
    String getPicture();

    AdvisorUserView getAdvisor();

    DocumentStatus.InternshipCompletionStatus getInternshipCompletionStatus();

    DocumentStatus.InternshipPaymentStatus getPaymentStatus();
}