package com.ahmadabbas.filetracking.backend.student.view;

import com.ahmadabbas.filetracking.backend.student.Student;
import com.blazebit.persistence.view.EntityView;
import com.blazebit.persistence.view.IdMapping;
import com.blazebit.persistence.view.Mapping;

@EntityView(Student.class)
public interface StudentView {
    @IdMapping
    String getId();

    @Mapping("user.id")
    Long getUserId();
}