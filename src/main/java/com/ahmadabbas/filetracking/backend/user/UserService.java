package com.ahmadabbas.filetracking.backend.user;

import com.ahmadabbas.filetracking.backend.user.payload.UserDto;
import com.ahmadabbas.filetracking.backend.user.payload.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public List<UserDto> getAllNonAdminUsers() {
        List<User> allNonAdminUsers = userRepository.findAllNonAdminUsers();
        return allNonAdminUsers.stream().map(userMapper::toDto).collect(Collectors.toList());
    }

    public Set<Role> getRoles(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("Should be logged in!");
        }
        return authentication.getAuthorities().stream()
                .map(s -> {
                    String replaced = s.toString().replace("ROLE_", "");
                    return Role.valueOf(replaced);
                }).collect(Collectors.toSet());
    }

    public Set<Role> getRoles(User user) {
        if (user == null) {
            throw new AccessDeniedException("Should be logged in!");
        }
        return user.getAuthorities().stream()
                .map(s -> {
                    String replaced = s.toString().replace("ROLE_", "");
                    return Role.valueOf(replaced);
                }).collect(Collectors.toSet());
    }

}
