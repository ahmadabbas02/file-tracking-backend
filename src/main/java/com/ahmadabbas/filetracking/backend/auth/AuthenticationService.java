package com.ahmadabbas.filetracking.backend.auth;

import com.ahmadabbas.filetracking.backend.auth.payload.AuthenticationRequest;
import com.ahmadabbas.filetracking.backend.auth.payload.AuthenticationResponse;
import com.ahmadabbas.filetracking.backend.token.Token;
import com.ahmadabbas.filetracking.backend.token.TokenRepository;
import com.ahmadabbas.filetracking.backend.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final TokenRepository tokenRepository;

    public AuthenticationResponse login(AuthenticationRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.loginId(),
                        request.password()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        User user = (User) authentication.getPrincipal();
        Map<String, Object> extraClaims = Map.of(
                "role", user.getRoles(),
                "name", user.getName()
        );
        String jwtToken = jwtService.generateToken(
                extraClaims,
                authentication
        );
        saveToken(user, jwtToken);
        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .build();
    }

    private void saveToken(User user, String jwtToken) {
        Token token = Token.builder()
                .user(user)
                .token(jwtToken)
                .expired(false)
                .blocked(false)
                .build();
        tokenRepository.save(token);
    }
}
