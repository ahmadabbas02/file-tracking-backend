package com.ahmadabbas.filetracking.backend.payload;

import com.ahmadabbas.filetracking.backend.enums.Role;
import com.ahmadabbas.filetracking.backend.validator.RoleTypeSubSet;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

    @NotEmpty
    private String loginId;

    @NotEmpty
    private String password;

    @RoleTypeSubSet(
            anyOf = {Role.ADMINISTRATOR, Role.CHAIR, Role.VICE_CHAR, Role.SECRETARY, Role.STUDENT}
    )
    private Role role;

}
