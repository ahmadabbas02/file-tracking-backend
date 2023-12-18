package com.ahmadabbas.filetracking.backend.student;

import com.ahmadabbas.filetracking.backend.advisor.Advisor;
import com.ahmadabbas.filetracking.backend.advisor.AdvisorRepository;
import com.ahmadabbas.filetracking.backend.advisor.AdvisorService;
import com.ahmadabbas.filetracking.backend.exception.DuplicateResourceException;
import com.ahmadabbas.filetracking.backend.exception.ResourceNotFoundException;
import com.ahmadabbas.filetracking.backend.student.payload.StudentCsvRepresentation;
import com.ahmadabbas.filetracking.backend.student.payload.StudentRegistrationRequest;
import com.ahmadabbas.filetracking.backend.user.Role;
import com.ahmadabbas.filetracking.backend.user.User;
import com.ahmadabbas.filetracking.backend.user.UserRepository;
import com.ahmadabbas.filetracking.backend.util.PageableUtil;
import com.ahmadabbas.filetracking.backend.util.payload.CsvUploadResponse;
import com.ahmadabbas.filetracking.backend.util.payload.PaginatedResponse;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class StudentService {
    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final AdvisorRepository advisorRepository;
    private final AdvisorService advisorService;

    private final PageableUtil pageableUtil;

    private final PasswordEncoder passwordEncoder;

    private final StudentDtoMapper studentDtoMapper;

    public StudentService(StudentRepository studentRepository, UserRepository userRepository,
                          AdvisorRepository advisorRepository,
                          AdvisorService advisorService, PageableUtil pageableUtil,
                          PasswordEncoder passwordEncoder, StudentDtoMapper studentDtoMapper) {
        this.studentRepository = studentRepository;
        this.userRepository = userRepository;
        this.advisorRepository = advisorRepository;
        this.advisorService = advisorService;
        this.pageableUtil = pageableUtil;
        this.passwordEncoder = passwordEncoder;
        this.studentDtoMapper = studentDtoMapper;
    }

    public Student getStudent(String id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "student with id %s not found".formatted(id)
                ));
    }

    public PaginatedResponse<StudentDto> getAllStudents(int pageNo, int pageSize, String sortBy, String order) {
        Pageable pageable = pageableUtil.getPageable(pageNo, pageSize, sortBy, order);

        Page<Student> studentPage = studentRepository.findAll(pageable);
        List<StudentDto> content = studentPage.getContent().stream().map(studentDtoMapper).toList();
        return new PaginatedResponse<>(
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

    public CsvUploadResponse uploadStudents(MultipartFile file) throws IOException {
        Set<StudentCsvRepresentation> studentCsvRepresentationSet = parseCsv(file);
        Map<Boolean, List<StudentCsvRepresentation>> partitionedStudents = studentCsvRepresentationSet
                .stream()
                .collect(Collectors.partitioningBy(
                        s -> s.getAdvisorId() == null
                                || s.getAdvisorId().isBlank()
                                || advisorRepository.existsById(s.getAdvisorId())
                ));
        Set<StudentCsvRepresentation> filteredStudents = new HashSet<>(partitionedStudents.get(true));
        Set<String> studentsWithWrongInformation = partitionedStudents.get(false)
                .stream()
                .map(StudentCsvRepresentation::getStudentId)
                .collect(Collectors.toSet());

        List<Student> students = filteredStudents
                .stream()
                .map(s -> {
                    Advisor advisor = null;
                    User user = User.builder()
                            .name(s.getName())
                            .email(s.getEmail())
                            .password(passwordEncoder.encode(s.getPassword()))
                            .role(Role.STUDENT)
                            .isEnabled(s.isEnabled())
                            .build();
                    if (!s.getAdvisorId().isBlank()) {
                        advisor = advisorService.findAdvisorByAdvisorId(s.getAdvisorId());
                    }
                    return Student.builder()
                            .id(s.getStudentId())
                            .user(user)
                            .advisor(advisor)
                            .department(s.getDepartment())
                            .year(s.getYear())
                            .picture(s.getPicture())
                            .build();
                }).toList();

        List<Student> savedStudents = studentRepository.saveAll(students);

        return CsvUploadResponse.builder()
                .successCount(savedStudents.size())
                .failedCount(studentsWithWrongInformation.size())
                .failedReason(studentsWithWrongInformation.isEmpty()
                        ? null
                        : "Failed to add students with IDs: " + studentsWithWrongInformation
                )
                .build();
    }

    private Set<StudentCsvRepresentation> parseCsv(MultipartFile file) throws IOException {
        try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            HeaderColumnNameMappingStrategy<StudentCsvRepresentation> strategy =
                    new HeaderColumnNameMappingStrategy<>();
            strategy.setType(StudentCsvRepresentation.class);
            CsvToBean<StudentCsvRepresentation> csvToBean =
                    new CsvToBeanBuilder<StudentCsvRepresentation>(reader)
                            .withMappingStrategy(strategy)
                            .withIgnoreEmptyLine(true)
                            .withIgnoreLeadingWhiteSpace(true)
                            .build();
            return new HashSet<>(csvToBean.parse());
        }
    }
}
