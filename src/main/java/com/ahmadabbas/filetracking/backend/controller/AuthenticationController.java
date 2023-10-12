package com.ahmadabbas.filetracking.backend.controller;

import com.ahmadabbas.filetracking.backend.payload.AuthenticationRequest;
import com.ahmadabbas.filetracking.backend.payload.AuthenticationResponse;
import com.ahmadabbas.filetracking.backend.payload.RegisterRequest;
import com.ahmadabbas.filetracking.backend.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest registerRequest) {
        var regResponse = authenticationService.register(registerRequest);

        return new ResponseEntity<>(regResponse, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody AuthenticationRequest authenticationRequest) {
        var authResponse = authenticationService.login(authenticationRequest);

        return ResponseEntity.ok(authResponse);
    }
}
