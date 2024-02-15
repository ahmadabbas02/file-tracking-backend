package com.ahmadabbas.filetracking.backend.auth.payload;

import jakarta.validation.constraints.NotEmpty;


public record AuthenticationRequest(@NotEmpty String loginId, @NotEmpty() String password) {

}
