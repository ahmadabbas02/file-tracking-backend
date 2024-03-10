package com.ahmadabbas.filetracking.backend.auth.payload;

import jakarta.validation.constraints.NotEmpty;


public record AuthenticationRequest(
        @NotEmpty(message = "loginId should not be empty") String loginId,
        @NotEmpty(message = "password should not be empty") String password
) {

}
