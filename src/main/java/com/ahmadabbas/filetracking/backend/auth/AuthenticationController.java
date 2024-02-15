package com.ahmadabbas.filetracking.backend.auth;

import com.ahmadabbas.filetracking.backend.auth.payload.AuthenticationRequest;
import com.ahmadabbas.filetracking.backend.auth.payload.AuthenticationResponse;
import com.ahmadabbas.filetracking.backend.user.Role;
import com.ahmadabbas.filetracking.backend.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final UserService userService;


    @Operation(
            summary = "Login to the system",
            description = """
                    Login to the system using `loginId` which be an email or a specific id like studentId, advisorId.
                    """
    )
    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody @Valid AuthenticationRequest authenticationRequest) {
        AuthenticationResponse authResponse = authenticationService.login(authenticationRequest);
        return ResponseEntity.ok(authResponse);
    }

    @Operation(
            summary = "Get roles",
            description = """
                    Returns a list of roles for the current logged in user.
                    """
    )
    @GetMapping("/roles")
    public ResponseEntity<Set<Role>> roles(Authentication authentication) {
        Set<Role> roles = userService.getRoles(authentication);
        return ResponseEntity.ok(roles);
    }
}
