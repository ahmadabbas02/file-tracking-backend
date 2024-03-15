package com.ahmadabbas.filetracking.backend.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    public Set<Role> getRoles(User user) {
        return user.getAuthorities().stream()
                .map(s -> {
                    String replaced = s.toString().replace("ROLE_", "");
                    return Role.valueOf(replaced);
                }).collect(Collectors.toSet());
    }

}
