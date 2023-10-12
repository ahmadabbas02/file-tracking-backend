package com.ahmadabbas.filetracking.backend.payload;

import com.ahmadabbas.filetracking.backend.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

    private String loginId;
    private String password;
    private Role role;

}
