package com.ahmadabbas.filetracking.backend.service;

import com.ahmadabbas.filetracking.backend.dto.UserDto;
import com.ahmadabbas.filetracking.backend.entity.User;
import com.ahmadabbas.filetracking.backend.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {
    private UserRepository userRepository;

    public UserDto createUser(UserDto userDto) {
        User user = UserDto.toEntity(userDto);
        var saved = userRepository.save(user);
        return UserDto.fromEntity(saved);
    }
}
