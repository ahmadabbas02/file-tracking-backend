package com.ahmadabbas.filetracking.backend.auth;

import com.ahmadabbas.filetracking.backend.auth.payload.*;
import com.ahmadabbas.filetracking.backend.user.Role;
import com.ahmadabbas.filetracking.backend.user.UserPrincipal;
import com.ahmadabbas.filetracking.backend.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication")
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
            summary = "Request activation email",
            description = """
                    Sends to the user's email a code that can be used to activate and set password for account.
                    """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully sent email"),
                    @ApiResponse(responseCode = "400", description = "Account is already activated"),
            }
    )
    @PostMapping("/activation-email")
    public ResponseEntity<ActivationEmailResponse> requestActivation(
            @RequestBody @Valid SendActivationEmailRequest activationEmailRequest) throws MessagingException {
        ActivationEmailResponse response = authenticationService.requestActivation(activationEmailRequest);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Activate account",
            description = """
                    Activate an account with the activation code received in email.
                    """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "Account successfully activated"),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Could not find a valid activationCode related to account or account related to email"
                    ),
            }
    )
    @PostMapping("/activate")
    public ResponseEntity<ActivationEmailResponse> activate(@RequestBody @Valid AccountActivationRequest activationRequest) {
        ActivationEmailResponse response = authenticationService.activateAccount(activationRequest);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Get roles",
            description = """
                    Returns a list of roles for the current logged in user.
                    """
    )
    @GetMapping("/roles")
    public ResponseEntity<Set<Role>> roles(@AuthenticationPrincipal UserPrincipal principal) {
        Set<Role> roles = userService.getRoles(principal.getUserEntity());
        return ResponseEntity.ok(roles);
    }
}
