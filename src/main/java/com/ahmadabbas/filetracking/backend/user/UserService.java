package com.ahmadabbas.filetracking.backend.user;

import com.ahmadabbas.filetracking.backend.exception.ResourceNotFoundException;
import com.ahmadabbas.filetracking.backend.user.payload.UserDto;
import com.ahmadabbas.filetracking.backend.user.payload.UserMapper;
import com.ahmadabbas.filetracking.backend.user.payload.UserUpdateDto;
import com.ahmadabbas.filetracking.backend.user.repository.UserRepository;
import com.ahmadabbas.filetracking.backend.util.PagingUtils;
import com.ahmadabbas.filetracking.backend.util.payload.PaginatedResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User getUserById(Long userId) {
        return userRepository.findUserById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "user with id `%s` not found".formatted(userId)
                ));
    }

    public Set<Role> getRoles(User user) {
        return user.getAuthorities().stream()
                .map(s -> {
                    String replaced = s.toString().replace("ROLE_", "");
                    return Role.valueOf(replaced);
                })
                .collect(Collectors.toSet());
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
            userPage = userRepository.findAll(name, roles, pageable);
        }

        if (userPage == null) {
            userPage = Page.empty();
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
    public User updateUser(Long userId, UserUpdateDto updateDto) {
        User userById = userRepository.findByIdLocked(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "user with id `%s` not found".formatted(userId)
                ));
        userMapper.partialUpdate(updateDto, userById);
        if (updateDto.password() != null) {
            userById.setPassword(passwordEncoder.encode(updateDto.password()));
        }
        return userRepository.save(userById);
    }
}
