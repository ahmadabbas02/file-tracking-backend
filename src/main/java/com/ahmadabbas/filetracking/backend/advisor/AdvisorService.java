package com.ahmadabbas.filetracking.backend.advisor;

import com.ahmadabbas.filetracking.backend.advisor.repository.AdvisorRepository;
import com.ahmadabbas.filetracking.backend.advisor.repository.AdvisorUserViewRepository;
import com.ahmadabbas.filetracking.backend.advisor.view.AdvisorUserView;
import com.ahmadabbas.filetracking.backend.exception.DuplicateResourceException;
import com.ahmadabbas.filetracking.backend.exception.ResourceNotFoundException;
import com.ahmadabbas.filetracking.backend.student.Student;
import com.ahmadabbas.filetracking.backend.student.repository.StudentRepository;
import com.ahmadabbas.filetracking.backend.user.Role;
import com.ahmadabbas.filetracking.backend.user.User;
import com.ahmadabbas.filetracking.backend.user.payload.UserRegistrationRequest;
import com.ahmadabbas.filetracking.backend.user.repository.UserRepository;
import com.ahmadabbas.filetracking.backend.util.PagingUtils;
import com.ahmadabbas.filetracking.backend.util.payload.PaginatedResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class AdvisorService {
    private final StudentRepository studentRepository;
    private final AdvisorRepository advisorRepository;
    private final AdvisorUserViewRepository advisorUserViewRepository;
    private final UserRepository userRepository;

    public Advisor getAdvisorByAdvisorId(String advisorId, User loggedInUser) {
        checkPermissions(advisorId, loggedInUser);
        return advisorRepository.findById(advisorId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "advisor with id `%s` not found".formatted(advisorId)
                ));
    }

    public AdvisorUserView getAdvisorByUserId(Long userId) {
        return advisorUserViewRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "advisor with user id `%s` not found".formatted(userId)
                ));
    }

    public AdvisorUserView getAdvisorViewByAdvisorId(String advisorId, User loggedInUser) {
        checkPermissions(advisorId, loggedInUser);
        return advisorUserViewRepository.findById(advisorId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "advisor with id `%s` not found".formatted(advisorId)
                ));
    }

    @Transactional
    public Advisor addAdvisor(UserRegistrationRequest userRegistrationRequest) {
        if (userRepository.existsByEmail(userRegistrationRequest.email())) {
            throw new DuplicateResourceException(
                    "email already taken"
            );
        }

        if (userRepository.existsByName(userRegistrationRequest.firstName(), userRegistrationRequest.lastName())) {
            throw new DuplicateResourceException(
                    "name already taken"
            );
        }

        User savedUser = userRepository.save(
                User.builder()
                        .firstName(userRegistrationRequest.firstName())
                        .lastName(userRegistrationRequest.lastName())
                        .email(userRegistrationRequest.email())
//                        .password(passwordEncoder.encode(userRegistrationRequest.password()))
                        .picture(userRegistrationRequest.picture())
                        .phoneNumber(userRegistrationRequest.phoneNumber())
                        .role(Role.ADVISOR)
                        .build()
        );

        return advisorRepository.save(
                Advisor.builder()
                        .user(savedUser)
                        .build()
        );
    }

    public PaginatedResponse<AdvisorUserView> getAllAdvisors(int pageNo,
                                                             int pageSize,
                                                             String sortBy,
                                                             String order,
                                                             String searchQuery) {
        Pageable pageable = PagingUtils.getPageable(pageNo, pageSize, sortBy, order);
        log.debug("Provided search query: '%s', getting all advisors..".formatted(searchQuery));
        searchQuery = searchQuery.trim();
        Page<AdvisorUserView> advisorPage = advisorRepository.findAllAdvisorsProjection(searchQuery, pageable);
        List<AdvisorUserView> content = advisorPage.getContent();
        return new PaginatedResponse<>(
                content,
                pageNo,
                pageSize,
                advisorPage.getTotalElements(),
                advisorPage.getTotalPages(),
                advisorPage.isLast()
        );
    }

    private void checkPermissions(String advisorId, User loggedInUser) {
        if (loggedInUser.isAdvisor()) {
            AdvisorUserView advisor = getAdvisorByUserId(loggedInUser.getId());
            if (!advisorId.equals(advisor.getId())) {
                throw new AccessDeniedException("not authorized, you can only get details about your own profile");
            }
        } else if (loggedInUser.isStudent()) {
            Student student = studentRepository.findByUserId(loggedInUser.getId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "student related with user id %s not found".formatted(loggedInUser.getId())
                    ));
            if (!student.getAdvisor().getId().equals(advisorId)) {
                throw new AccessDeniedException("not authorized, you can only get details for your own advisor profile");
            }
        }
    }
}
