package com.ahmadabbas.filetracking.backend.advisor;

import com.ahmadabbas.filetracking.backend.advisor.payload.AdvisorDto;
import com.ahmadabbas.filetracking.backend.advisor.payload.AdvisorMapper;
import com.ahmadabbas.filetracking.backend.advisor.payload.AdvisorRegistrationRequest;
import com.ahmadabbas.filetracking.backend.exception.DuplicateResourceException;
import com.ahmadabbas.filetracking.backend.exception.ResourceNotFoundException;
import com.ahmadabbas.filetracking.backend.user.Role;
import com.ahmadabbas.filetracking.backend.user.User;
import com.ahmadabbas.filetracking.backend.user.UserRepository;
import com.ahmadabbas.filetracking.backend.util.PageableUtil;
import com.ahmadabbas.filetracking.backend.util.payload.PaginatedResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AdvisorService {
    private final PasswordEncoder passwordEncoder;
    private final AdvisorRepository advisorRepository;
    private final UserRepository userRepository;

    public Advisor findAdvisorByAdvisorId(String advisorId) {
        return advisorRepository.findById(advisorId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "advisor with id `%s` not found".formatted(advisorId)
                ));
    }

    @Transactional
    public Advisor addAdvisor(AdvisorRegistrationRequest advisorRegistrationRequest) {
        if (userRepository.existsByEmail(advisorRegistrationRequest.email())) {
            throw new DuplicateResourceException(
                    "email already taken"
            );
        }

        if (userRepository.existsByName(advisorRegistrationRequest.name())) {
            throw new DuplicateResourceException(
                    "name already taken"
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

    public PaginatedResponse<AdvisorDto> getAllAdvisors(int pageNo, int pageSize, String sortBy, String order) {
        Pageable pageable = PageableUtil.getPageable(pageNo, pageSize, sortBy, order);
        Page<Advisor> studentPage = advisorRepository.findAll(pageable);
        List<AdvisorDto> content = studentPage.getContent()
                .stream()
                .map(AdvisorMapper.INSTANCE::toDto)
                .toList();
        return new PaginatedResponse<>(
                content,
                pageNo,
                pageSize,
                studentPage.getTotalElements(),
                studentPage.getTotalPages(),
                studentPage.isLast()
        );
    }
}
