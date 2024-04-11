package com.ahmadabbas.filetracking.backend.advisor.payload;

import com.ahmadabbas.filetracking.backend.user.Role;
import com.ahmadabbas.filetracking.backend.user.payload.UserRegistrationRequest;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public final class AdvisorRegistrationRequest extends UserRegistrationRequest {

    public AdvisorRegistrationRequest(String name, String surname, String email, String picture, String phoneNumber) {
        super(name, surname, email, picture, phoneNumber, Role.ADVISOR);
    }

    @Override
    public String toString() {
        return super.toString();
    }

}
