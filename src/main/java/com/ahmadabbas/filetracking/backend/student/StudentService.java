package com.ahmadabbas.filetracking.backend.student;

import com.ahmadabbas.filetracking.backend.advisor.Advisor;
import com.ahmadabbas.filetracking.backend.advisor.AdvisorService;
import com.ahmadabbas.filetracking.backend.exception.DuplicateResourceException;
import com.ahmadabbas.filetracking.backend.exception.ResourceNotFoundException;
import com.ahmadabbas.filetracking.backend.student.payload.StudentRegistrationRequest;
import com.ahmadabbas.filetracking.backend.user.Role;
import com.ahmadabbas.filetracking.backend.user.User;
import com.ahmadabbas.filetracking.backend.user.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class StudentService {
    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final AdvisorService advisorService;
    private final PasswordEncoder passwordEncoder;

    public StudentService(StudentRepository studentRepository, UserRepository userRepository,
                          AdvisorService advisorService, PasswordEncoder passwordEncoder) {
        this.studentRepository = studentRepository;
        this.userRepository = userRepository;
        this.advisorService = advisorService;
        this.passwordEncoder = passwordEncoder;
    }

    public Student getStudent(String id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "student with id [%s] not found".formatted(id)
                ));
    }

    @Transactional
    public Student addStudent(StudentRegistrationRequest studentRegistrationRequest) {
        if (userRepository.existsByEmail(studentRegistrationRequest.email())) {
            throw new DuplicateResourceException(
                    "email already taken"
            );
        }

        Advisor advisor = advisorService.findAdvisorByAdvisorId(studentRegistrationRequest.advisorId());

        User user = User.builder()
                .name(studentRegistrationRequest.name())
                .email(studentRegistrationRequest.email())
                .password(passwordEncoder.encode(studentRegistrationRequest.password()))
                .role(Role.STUDENT)
                .build();
        User savedUser = userRepository.save(user);

        Student student = Student.builder()
                .advisor(advisor)
                .department(studentRegistrationRequest.department())
                .year(studentRegistrationRequest.year())
                .picture(studentRegistrationRequest.picture())
                .user(savedUser)
                .build();
        return studentRepository.save(student);
    }
}
