package com.ahmadabbas.filetracking.backend.user;

import com.ahmadabbas.filetracking.backend.exception.APIException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllNonAdminUsers() {
        return userRepository.findAllNonAdminUsers()
                .orElseThrow(() -> new APIException(HttpStatus.NOT_FOUND, "Failed to get non admin users"));
    }


}
