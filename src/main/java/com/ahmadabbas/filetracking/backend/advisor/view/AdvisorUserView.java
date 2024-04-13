package com.ahmadabbas.filetracking.backend.advisor.view;

import com.ahmadabbas.filetracking.backend.advisor.Advisor;
import com.blazebit.persistence.view.EntityView;
import com.blazebit.persistence.view.IdMapping;
import com.blazebit.persistence.view.Mapping;
import com.fasterxml.jackson.annotation.JsonIgnore;

@EntityView(Advisor.class)
public interface AdvisorUserView {
    @IdMapping
    String getId();

    @JsonIgnore
    @Mapping("user.id")
    Long getUserId();

    @Mapping("user.firstName")
    String getFirstName();

    @Mapping("user.lastName")
    String getLastName();

    @Mapping("CONCAT(user.firstName, ' ' ,user.lastName)")
    String getFullName();

    @Mapping("user.picture")
    String getPicture();
}