package com.ahmadabbas.filetracking.backend.service;

import com.ahmadabbas.filetracking.backend.entity.User;
import com.ahmadabbas.filetracking.backend.exception.APIException;
import com.ahmadabbas.filetracking.backend.payload.AuthenticationRequest;
import com.ahmadabbas.filetracking.backend.payload.AuthenticationResponse;
import com.ahmadabbas.filetracking.backend.payload.RegisterRequest;
import com.ahmadabbas.filetracking.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public String register(RegisterRequest request) {
        if (userRepository.existsByLoginId(request.getLoginId())) {
            throw new APIException(HttpStatus.BAD_REQUEST, "Login Id already exists!");
        }

        var user = User.builder()
                .loginId(request.getLoginId())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .build();

        userRepository.save(user);

        return "User registered successfully!";
    }

    public AuthenticationResponse login(AuthenticationRequest request) {
        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getLoginId(),
                        request.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        var jwtToken = jwtService.generateToken(authentication);
        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .build();
    }
}
