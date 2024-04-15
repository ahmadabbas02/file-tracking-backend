package com.ahmadabbas.filetracking.backend.user;

import com.ahmadabbas.filetracking.backend.advisor.AdvisorService;
import com.ahmadabbas.filetracking.backend.exception.APIException;
import com.ahmadabbas.filetracking.backend.exception.DuplicateResourceException;
import com.ahmadabbas.filetracking.backend.exception.ResourceNotFoundException;
import com.ahmadabbas.filetracking.backend.user.payload.UserDto;
import com.ahmadabbas.filetracking.backend.user.payload.UserMapper;
import com.ahmadabbas.filetracking.backend.user.payload.UserRegistrationRequest;
import com.ahmadabbas.filetracking.backend.user.payload.UserUpdateDto;
import com.ahmadabbas.filetracking.backend.user.repository.UserRepository;
import com.ahmadabbas.filetracking.backend.util.PagingUtils;
import com.ahmadabbas.filetracking.backend.util.payload.PaginatedResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class UserService {

    public static final Role[] ALLOWED_REGISTRATION_ROLES = {Role.SECRETARY, Role.ADMINISTRATOR, Role.CHAIR, Role.ADVISOR};

    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AdvisorService advisorService;

    public User getUserById(Long userId) {
        return userRepository.findUserById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "user with id `%s` not found".formatted(userId)
                ));
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "user with email `%s` not found".formatted(email)
                ));
    }

    public Set<Role> getRoles(User user) {
        return user.getRoles();
    }

    public PaginatedResponse<UserDto> getAllUsers(int pageNo,
                                                  int pageSize,
                                                  String sortBy,
                                                  String order,
                                                  String name,
                                                  String roleId,
                                                  List<Role> roles) {
        Pageable pageable = PagingUtils.getPageable(pageNo, pageSize, sortBy, order);
        Page<User> userPage;
        if (!roleId.isEmpty()) {
            if (roleId.toLowerCase().startsWith("ap")) {
                log.debug("getting all advisors by roleId `{}`", roleId);
                userPage = userRepository.findAllAdvisor(roleId, pageable);
            } else {
                log.debug("getting all students by roleId `{}`", roleId);
                userPage = userRepository.findAllStudent(roleId, pageable);
            }
        } else {
            name = name.trim();
            userPage = userRepository.findAll(name, roles, pageable);
        }

        List<UserDto> content = userPage.getContent()
                .stream()
                .map(userMapper::toDto)
                .toList();
        return new PaginatedResponse<>(
                content,
                pageNo,
                pageSize,
                userPage.getTotalElements(),
                userPage.getTotalPages(),
                userPage.isLast()
        );
    }

    @Transactional
    public User addUser(UserRegistrationRequest registrationRequest) {
        if (registrationRequest.role() == Role.ADVISOR) {
            return advisorService.addAdvisor(registrationRequest).getUser();
        }

        if (userRepository.existsByEmail(registrationRequest.email())) {
            throw new DuplicateResourceException(
                    "email already taken"
            );
        }
        List<Role> allowedRoles = Arrays.asList(ALLOWED_REGISTRATION_ROLES);
        if (!allowedRoles.contains(registrationRequest.role())) {
            throw new APIException(
                    HttpStatus.BAD_REQUEST,
                    "Only %s values for role are allowed!".formatted(allowedRoles)
            );
        }
        return userRepository.save(
                User.builder()
                        .firstName(registrationRequest.firstName())
                        .lastName(registrationRequest.lastName())
                        .email(registrationRequest.email())
                        .picture(registrationRequest.picture())
                        .phoneNumber(registrationRequest.phoneNumber())
                        .role(registrationRequest.role())
                        .build()
        );
    }

    @Transactional
    public User updateUser(Long userId, UserUpdateDto updateDto) {
        User user = userRepository.lockUserById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "user with id `%s` not found".formatted(userId)
                ));
        userMapper.partialUpdate(updateDto, user);
        if (updateDto.password() != null) {
            user.setPassword(passwordEncoder.encode(updateDto.password()));
        }
        return userRepository.save(user);
    }

}
