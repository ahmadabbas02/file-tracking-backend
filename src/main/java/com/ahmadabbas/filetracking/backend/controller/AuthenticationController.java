package com.ahmadabbas.filetracking.backend.controller;

import com.ahmadabbas.filetracking.backend.payload.AuthenticationRequest;
import com.ahmadabbas.filetracking.backend.payload.AuthenticationResponse;
import com.ahmadabbas.filetracking.backend.payload.RegisterRequest;
import com.ahmadabbas.filetracking.backend.payload.UserDto;
import com.ahmadabbas.filetracking.backend.service.AuthenticationService;
import com.ahmadabbas.filetracking.backend.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody @Valid RegisterRequest registerRequest) {

        String regResponse = authenticationService.register(registerRequest);

        return new ResponseEntity<>(regResponse, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody @Valid AuthenticationRequest authenticationRequest) {
        AuthenticationResponse authResponse = authenticationService.login(authenticationRequest);

        return ResponseEntity.ok(authResponse);
    }

    @GetMapping("/roles")
    public ResponseEntity<List<UserDto>> roles() {
        List<UserDto> users = userService.getAllNonAdminUsers();

        return ResponseEntity.ok(users);
    }
}
