package com.ahmadabbas.filetracking.backend.service;

import com.ahmadabbas.filetracking.backend.entity.User;
import com.ahmadabbas.filetracking.backend.enums.Role;
import com.ahmadabbas.filetracking.backend.exception.APIException;
import com.ahmadabbas.filetracking.backend.payload.UserDto;
import com.ahmadabbas.filetracking.backend.repository.UserRepository;
import com.ahmadabbas.filetracking.backend.validator.ValueOfEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public List<UserDto> getAllNonAdminUsers() {
        List<User> allNonAdminUsers = userRepository.findAllNonAdminUsers()
                .orElseThrow(() -> new APIException(HttpStatus.NOT_FOUND, "Failed to get non admin users"));

        return allNonAdminUsers.stream().map(User::toDto).toList();
    }
}
