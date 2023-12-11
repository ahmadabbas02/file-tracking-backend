package com.ahmadabbas.filetracking.backend.user;


import com.ahmadabbas.filetracking.backend.advisor.AdvisorRepository;
import com.ahmadabbas.filetracking.backend.student.StudentRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final AdvisorRepository advisorRepository;

    public UserDetailsServiceImpl(UserRepository userRepository,
                                  StudentRepository studentRepository,
                                  AdvisorRepository advisorRepository) {
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
        this.advisorRepository = advisorRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var user = userRepository.findByEmail(username);
        if (user.isPresent()) {
            return user.get();
        }
        var student = studentRepository.findById(username);
        if (student.isPresent()) {
            return student.get().getUser();
        }
        var advisor = advisorRepository.findById(username);
        if (advisor.isPresent()) {
            return advisor.get().getUser();
        }
        throw new UsernameNotFoundException("Email/ID " + username + " not found!");
    }
}
