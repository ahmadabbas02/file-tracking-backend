package com.ahmadabbas.filetracking.backend.auth;

import com.ahmadabbas.filetracking.backend.auth.payload.AuthenticationRequest;
import com.ahmadabbas.filetracking.backend.auth.payload.AuthenticationResponse;
import com.ahmadabbas.filetracking.backend.user.User;
import com.ahmadabbas.filetracking.backend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse login(AuthenticationRequest request) {
        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getLoginId(),
                        request.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        var user = (User) authentication.getPrincipal();
        var jwtToken = jwtService.generateToken(
                Map.of("role", user.getRole()),
                authentication
        );
        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .build();
    }
}
