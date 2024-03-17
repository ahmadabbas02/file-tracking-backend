package com.ahmadabbas.filetracking.backend.user;

import com.ahmadabbas.filetracking.backend.advisor.*;
import com.ahmadabbas.filetracking.backend.student.*;
import com.ahmadabbas.filetracking.backend.user.payload.*;
import com.ahmadabbas.filetracking.backend.user.repository.UserRepository;
import com.ahmadabbas.filetracking.backend.util.PageableUtil;
import com.ahmadabbas.filetracking.backend.util.payload.PaginatedResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final AdvisorRepository advisorRepository;
    private final StudentRepository studentRepository;

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
        Pageable pageable = PageableUtil.getPageable(pageNo, pageSize, sortBy, order);
        Page<User> userPage;
        if (!name.isEmpty()) {
            if (roles.isEmpty()) {
                log.debug("getting all users by name `{}`", name);
                userPage = userRepository.findAllByNameContains(name, pageable);
            } else {
                log.debug("getting all users by name `{}` and roles {}", name, roles);
                userPage = userRepository.findAllByNameAndRoles(name, roles, pageable);
            }
        } else if (!roleId.isEmpty()) {
            if (roleId.startsWith("AP")) {
                log.debug("getting all advisors by roleId `{}`", roleId);
                Page<Advisor> advisorPage = advisorRepository.findAllByIdStartsWith(roleId, pageable);
                List<User> users = advisorPage.getContent().stream().map(Advisor::getUser).toList();
                return new PaginatedResponse<>(
                        users.stream().map(userMapper::toDto).toList(),
                        pageNo,
                        pageSize,
                        advisorPage.getTotalElements(),
                        advisorPage.getTotalPages(),
                        advisorPage.isLast()
                );
            } else {
                log.debug("getting all students by roleId `{}`", roleId);
                Page<Student> studentPage = studentRepository.findAllByIdStartsWith(roleId, pageable);
                List<User> users = studentPage.getContent().stream().map(Student::getUser).toList();
                return new PaginatedResponse<>(
                        users.stream().map(userMapper::toDto).toList(),
                        pageNo,
                        pageSize,
                        studentPage.getTotalElements(),
                        studentPage.getTotalPages(),
                        studentPage.isLast()
                );
            }
        } else {
            if (roles.isEmpty()) {
                log.debug("getting all users..");
                userPage = userRepository.findAll(pageable);
            } else {
                log.debug("getting all users by roles {}..", roles);
                userPage = userRepository.findAllByRoles(roles, pageable);
            }
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
}
