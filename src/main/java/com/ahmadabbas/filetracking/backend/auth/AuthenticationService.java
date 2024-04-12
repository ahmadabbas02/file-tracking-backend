package com.ahmadabbas.filetracking.backend.auth;

import com.ahmadabbas.filetracking.backend.auth.activation.ActivationCode;
import com.ahmadabbas.filetracking.backend.auth.activation.ActivationCodeRepository;
import com.ahmadabbas.filetracking.backend.auth.payload.*;
import com.ahmadabbas.filetracking.backend.auth.token.Token;
import com.ahmadabbas.filetracking.backend.auth.token.TokenRepository;
import com.ahmadabbas.filetracking.backend.email.EmailService;
import com.ahmadabbas.filetracking.backend.email.EmailTemplate;
import com.ahmadabbas.filetracking.backend.exception.APIException;
import com.ahmadabbas.filetracking.backend.exception.ResourceNotFoundException;
import com.ahmadabbas.filetracking.backend.user.User;
import com.ahmadabbas.filetracking.backend.user.UserService;
import com.ahmadabbas.filetracking.backend.user.repository.UserRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final TokenRepository tokenRepository;
    private final EmailService emailService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final ActivationCodeRepository activationCodeRepository;

    @Value("${application.mailing.frontend.activation-url}")
    private String activationUrl;
    private final UserRepository userRepository;

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
                "name", user.getFullName()
        );
        String jwtToken = jwtService.buildToken(
                extraClaims,
                authentication
        );
        saveToken(user, jwtToken);
        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .build();
    }

    public ActivationEmailResponse requestActivation(SendActivationEmailRequest activationRequest) throws MessagingException {
        User user = userService.getUserByEmail(activationRequest.email());
        if (user.isCredentialsNonExpired()) {
            throw new APIException(HttpStatus.BAD_REQUEST, "Account is already activated!");
        }
        // TODO: enable later
        /*if (activationCodeRepository.existsByEmailNotExpired(activationRequest.email(), LocalDateTime.now())) {
            throw new APIException(HttpStatus.BAD_REQUEST, "Code already sent! Please try again later.");
        }*/
        sendCodeEmail(user);
        return new ActivationEmailResponse("Please allow up to 5 minutes for the email to be received" +
                                           "and don't forget to check the spam folder!");
    }

    public ActivationEmailResponse activateAccount(AccountActivationRequest activationRequest) {
        ActivationCode activationCode = activationCodeRepository.findByCodeAndEmailNotExpired(
                        activationRequest.code(),
                        activationRequest.email(),
                        LocalDateTime.now()
                )
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Code could have been expired or is invalid"
                ));
        User user = userService.getUserByEmail(activationRequest.email());
        user.setPassword(passwordEncoder.encode(activationRequest.password()));
        user.setCredentialsNonExpired(true);
        userRepository.save(user);
        activationCodeRepository.delete(activationCode);
        return new ActivationEmailResponse("Account has been activated successfully!");
    }

    public void sendCodeEmail(User user) throws MessagingException {
        String activationCode = generateAndSaveActivationCode(user);

        emailService.sendEmail(
                user.getEmail(),
                user.getFullName(),
                EmailTemplate.ACTIVATE_ACCOUNT,
                activationCode,
                activationUrl,
                "Account Activation"
        );
    }

    private void saveToken(User user, String jwtToken) {
        Token token = Token.builder()
                .token(jwtToken)
                .expired(false)
                .blocked(false)
                .user(user)
                .build();
        tokenRepository.save(token);
    }

    private String generateAndSaveActivationCode(User user) {
        String generatedCode = generateCode(8);
        ActivationCode activationCode = ActivationCode.builder()
                .code(generatedCode)
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .user(user)
                .build();
        return activationCodeRepository.save(activationCode).getCode();
    }

    private String generateCode(int length) {
        char[] characters = IntStream.concat(
                        IntStream.rangeClosed('0', '9'),
                        IntStream.rangeClosed('A', 'Z')
                ).mapToObj(c -> (char) c + "")
                .collect(Collectors.joining())
                .toCharArray();
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(characters.length);
            sb.append(characters[randomIndex]);
        }
        return sb.toString();
    }

}
