package com.ahmadabbas.filetracking.backend.student;

import com.ahmadabbas.filetracking.backend.advisor.Advisor;
import com.ahmadabbas.filetracking.backend.advisor.AdvisorService;
import com.ahmadabbas.filetracking.backend.exception.DuplicateResourceException;
import com.ahmadabbas.filetracking.backend.exception.ResourceNotFoundException;
import com.ahmadabbas.filetracking.backend.student.payload.StudentRegistrationRequest;
import com.ahmadabbas.filetracking.backend.student.payload.StudentResponse;
import com.ahmadabbas.filetracking.backend.user.Role;
import com.ahmadabbas.filetracking.backend.user.User;
import com.ahmadabbas.filetracking.backend.user.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentService {
    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final AdvisorService advisorService;
    private final PasswordEncoder passwordEncoder;
    private final StudentDtoMapper studentDtoMapper;

    public StudentService(StudentRepository studentRepository, UserRepository userRepository,
                          AdvisorService advisorService, PasswordEncoder passwordEncoder, StudentDtoMapper studentDtoMapper) {
        this.studentRepository = studentRepository;
        this.userRepository = userRepository;
        this.advisorService = advisorService;
        this.passwordEncoder = passwordEncoder;
        this.studentDtoMapper = studentDtoMapper;
    }

    public Student getStudent(String id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "student with id %s not found".formatted(id)
                ));
    }

    public StudentResponse getAllStudents(int pageNo, int pageSize, String sortBy, String order) {
        Sort sort = order.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        Page<Student> studentPage = studentRepository.findAll(pageable);
        List<StudentDto> content = studentPage.getContent().stream().map(studentDtoMapper).toList();
        return new StudentResponse(
                content,
                pageNo,
                pageSize,
                studentPage.getTotalElements(),
                studentPage.getTotalPages(),
                studentPage.isLast()
        );
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
