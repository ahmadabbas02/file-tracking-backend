package com.ahmadabbas.filetracking.backend.advisor;

import com.ahmadabbas.filetracking.backend.advisor.payload.AdvisorRegistrationRequest;
import com.ahmadabbas.filetracking.backend.exception.DuplicateResourceException;
import com.ahmadabbas.filetracking.backend.exception.ResourceNotFoundException;
import com.ahmadabbas.filetracking.backend.user.Role;
import com.ahmadabbas.filetracking.backend.user.User;
import com.ahmadabbas.filetracking.backend.user.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AdvisorService {
    private final PasswordEncoder passwordEncoder;
    private final AdvisorRepository advisorRepository;
    private final UserRepository userRepository;


    public AdvisorService(AdvisorRepository advisorRepository, PasswordEncoder passwordEncoder,
                          UserRepository userRepository) {
        this.passwordEncoder = passwordEncoder;
        this.advisorRepository = advisorRepository;
        this.userRepository = userRepository;
    }

    public Advisor findAdvisorByUserId(Long userId) {
        return advisorRepository.findAdvisorByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "advisor with user id [%s] not found".formatted(userId)
                ));
    }

    public Advisor findAdvisorByAdvisorId(String advisorId) {
        return advisorRepository.findById(advisorId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "advisor with id [%s] not found".formatted(advisorId)
                ));
    }

    @Transactional
    public Advisor addAdvisor(AdvisorRegistrationRequest advisorRegistrationRequest) {
        if (userRepository.existsByEmail(advisorRegistrationRequest.email())) {
            throw new DuplicateResourceException(
                    "email already taken"
            );
        }

        User savedUser = userRepository.save(
                User.builder()
                        .name(advisorRegistrationRequest.name())
                        .email(advisorRegistrationRequest.email())
                        .password(passwordEncoder.encode(advisorRegistrationRequest.password()))
                        .role(Role.ADVISOR)
                        .build()
        );

        return advisorRepository.save(
                Advisor.builder()
                        .user(savedUser)
                        .build()
        );
    }
}
