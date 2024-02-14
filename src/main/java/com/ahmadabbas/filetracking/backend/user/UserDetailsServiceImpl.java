package com.ahmadabbas.filetracking.backend.user;


import com.ahmadabbas.filetracking.backend.advisor.Advisor;
import com.ahmadabbas.filetracking.backend.advisor.AdvisorRepository;
import com.ahmadabbas.filetracking.backend.student.Student;
import com.ahmadabbas.filetracking.backend.student.StudentRepository;
import lombok.RequiredArgsConstructor;
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
        Optional<User> user = userRepository.findByEmail(username);
        if (user.isPresent()) {
            return user.get();
        }
        Optional<Student> student = studentRepository.findById(username);
        if (student.isPresent()) {
            return student.get().getUser();
        }
        Optional<Advisor> advisor = advisorRepository.findById(username);
        if (advisor.isPresent()) {
            return advisor.get().getUser();
        }
        throw new UsernameNotFoundException("Email/ID %s not found!".formatted(username));
    }

}
