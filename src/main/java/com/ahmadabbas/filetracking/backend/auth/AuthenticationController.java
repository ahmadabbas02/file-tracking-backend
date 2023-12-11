package com.ahmadabbas.filetracking.backend.auth;

import com.ahmadabbas.filetracking.backend.payload.AuthenticationRequest;
import com.ahmadabbas.filetracking.backend.payload.AuthenticationResponse;
import com.ahmadabbas.filetracking.backend.user.UserDto;
import com.ahmadabbas.filetracking.backend.user.UserDtoMapper;
import com.ahmadabbas.filetracking.backend.user.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final UserService userService;
    private final UserDtoMapper userDtoMapper;

    public AuthenticationController(AuthenticationService authenticationService, UserService userService,
                                    UserDtoMapper userDtoMapper) {
        this.authenticationService = authenticationService;
        this.userService = userService;
        this.userDtoMapper = userDtoMapper;
    }

//    @PostMapping("/register")
//    public ResponseEntity<String> register(@RequestBody @Valid RegisterRequest registerRequest) {
//        String regResponse = authenticationService.register(registerRequest);
//
//        return new ResponseEntity<>(regResponse, HttpStatus.CREATED);
//    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody @Valid AuthenticationRequest authenticationRequest) {
        AuthenticationResponse authResponse = authenticationService.login(authenticationRequest);
        return ResponseEntity.ok(authResponse);
    }

    @GetMapping("/roles")
    public ResponseEntity<List<UserDto>> roles() {
        List<UserDto> users = userService.getAllNonAdminUsers().stream().map(userDtoMapper).collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }
}
