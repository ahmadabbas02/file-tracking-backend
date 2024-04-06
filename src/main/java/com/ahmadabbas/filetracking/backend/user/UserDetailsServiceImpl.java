package com.ahmadabbas.filetracking.backend.user;


import com.ahmadabbas.filetracking.backend.advisor.Advisor;
import com.ahmadabbas.filetracking.backend.advisor.repository.AdvisorRepository;
import com.ahmadabbas.filetracking.backend.student.Student;
import com.ahmadabbas.filetracking.backend.student.repository.StudentRepository;
import com.ahmadabbas.filetracking.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final AdvisorRepository advisorRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // we only access normal user_id logins through jwt
        if (username.startsWith("jwt:")) {
            username = username.replace("jwt:", "");
            if (username.length() < 8) {
                // handle cases of login for admin, chair, secretary etc
                Optional<User> user = userRepository.findById(Long.valueOf(username));
                if (user.isPresent()) {
                    return user.get();
                }
            } else {
                User user = getUser(username);
                if (user != null) {
                    return user;
                }
            }
        } else {
            User user = getUser(username);
            if (user != null) {
                return user;
            }
        }
        throw new UsernameNotFoundException("Email/ID %s not found!".formatted(username));
    }

    private User getUser(String username) {
        EmailValidator emailValidator = EmailValidator.getInstance(false);
        if (emailValidator.isValid(username)) {
            Optional<User> user = userRepository.findByEmail(username);
            if (user.isPresent()) {
                return user.get();
            }
        } else if (username.startsWith("AP")) {
            Optional<Advisor> advisor = advisorRepository.findById(username);
            if (advisor.isPresent()) {
                return advisor.get().getUser();
            }
        } else {
            Optional<Student> student = studentRepository.findById(username);
            if (student.isPresent()) {
                return student.get().getUser();
            }
        }
        return null;
    }

}
