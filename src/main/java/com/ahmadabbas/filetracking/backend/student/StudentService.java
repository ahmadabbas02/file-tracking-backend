package com.ahmadabbas.filetracking.backend.student;

import com.ahmadabbas.filetracking.backend.advisor.Advisor;
import com.ahmadabbas.filetracking.backend.advisor.AdvisorService;
import com.ahmadabbas.filetracking.backend.advisor.repository.AdvisorRepository;
import com.ahmadabbas.filetracking.backend.advisor.view.AdvisorUserView;
import com.ahmadabbas.filetracking.backend.document.base.DocumentStatus;
import com.ahmadabbas.filetracking.backend.exception.APIException;
import com.ahmadabbas.filetracking.backend.exception.DuplicateResourceException;
import com.ahmadabbas.filetracking.backend.exception.ResourceNotFoundException;
import com.ahmadabbas.filetracking.backend.student.payload.StudentCsvRepresentation;
import com.ahmadabbas.filetracking.backend.student.payload.StudentMapper;
import com.ahmadabbas.filetracking.backend.student.payload.StudentRegistrationRequest;
import com.ahmadabbas.filetracking.backend.student.payload.StudentUpdateDto;
import com.ahmadabbas.filetracking.backend.student.repository.StudentRepository;
import com.ahmadabbas.filetracking.backend.student.repository.StudentViewRepository;
import com.ahmadabbas.filetracking.backend.student.view.StudentAdvisorView;
import com.ahmadabbas.filetracking.backend.student.view.StudentView;
import com.ahmadabbas.filetracking.backend.user.Role;
import com.ahmadabbas.filetracking.backend.user.User;
import com.ahmadabbas.filetracking.backend.user.UserService;
import com.ahmadabbas.filetracking.backend.user.repository.UserRepository;
import com.ahmadabbas.filetracking.backend.util.PagingUtils;
import com.ahmadabbas.filetracking.backend.util.payload.CsvUploadResponse;
import com.ahmadabbas.filetracking.backend.util.payload.PaginatedResponse;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@Slf4j
@RequiredArgsConstructor
public class StudentService {
    private final StudentMapper studentMapper;
    private final UserRepository userRepository;
    private final AdvisorRepository advisorRepository;
    private final AdvisorService advisorService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final StudentRepository studentRepository;
    private final StudentViewRepository studentViewRepository;

    public StudentAdvisorView getStudentView(String studentId, User loggedInUser) {
        if (loggedInUser.isStudent()) {
            StudentAdvisorView student = getStudentAdvisorViewByUserId(loggedInUser.getId());
            if (!studentId.equals(student.getId())) {
                throw new AccessDeniedException("not authorized, you can only get details about your own profile");
            }
            return student;
        } else if (loggedInUser.isAdvisor()) {
            StudentAdvisorView student = getStudentAdvisorViewByStudentId(studentId);
            if (student.getAdvisor().getUserId().equals(loggedInUser.getId())) {
                return student;
            } else {
                throw new AccessDeniedException("not authorized, you can only get details about your own students");
            }
        }
        return getStudentAdvisorViewByStudentId(studentId);
    }

    public Student getStudent(String studentId, User loggedInUser) {
        if (loggedInUser.isStudent()) {
            Student student = getStudentByUserId(loggedInUser.getId());
            if (!studentId.equals(student.getId())) {
                throw new AccessDeniedException("not authorized, you can only get details about your own profile");
            }
            return student;
        } else if (loggedInUser.isAdvisor()) {
            Student student = getStudentByStudentId(studentId);
            if (student.getAdvisor().getUser().getId().equals(loggedInUser.getId())) {
                return student;
            } else {
                throw new AccessDeniedException("not authorized, you can only get details about your own students");
            }
        }
        return getStudentByStudentId(studentId);
    }

    public Student getStudentByUserId(Long userId) {
        return studentRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "student with user id `%s` not found".formatted(userId)
                ));
    }

    public Student getStudentByStudentId(String studentId) {
        return studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "student with id `%s` not found".formatted(studentId)
                ));
    }

    public StudentView getStudentViewByUserId(Long userId) {
        return studentViewRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "student with user id `%s` not found".formatted(userId)
                ));
    }

    public StudentAdvisorView getStudentAdvisorViewByStudentId(String studentId) {
        return studentRepository.getStudentViewById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "student with id `%s` not found".formatted(studentId)
                ));
    }

    public StudentAdvisorView getStudentAdvisorViewByUserId(Long userId) {
        return studentRepository.getStudentViewByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "student with user id `%s` not found".formatted(userId)
                ));
    }

    public PaginatedResponse<StudentAdvisorView> getAllStudents(User loggedInUser,
                                                                int pageNo,
                                                                int pageSize,
                                                                String sortBy,
                                                                String order,
                                                                String searchQuery,
                                                                String advisorId,
                                                                List<String> programs,
                                                                List<DocumentStatus.InternshipCompletionStatus> completionStatuses) {
        Pageable pageable = PagingUtils.getPageable(pageNo, pageSize, sortBy, order);
        Page<StudentAdvisorView> studentPage;
        log.debug("Logged in user = %s".formatted(loggedInUser));
        Set<Role> roles = userService.getRoles(loggedInUser);
        log.debug("Roles = %s".formatted(roles));
        searchQuery = searchQuery.trim();
        if (roles.contains(Role.ADVISOR)) {
            AdvisorUserView advisor = advisorService.getAdvisorByUserId(loggedInUser.getId());
            studentPage = studentRepository.getAllStudents(searchQuery,
                    advisor.getId(),
                    programs,
                    completionStatuses,
                    pageable);
        } else {
            studentPage = studentRepository.getAllStudents(searchQuery,
                    advisorId,
                    programs,
                    completionStatuses,
                    pageable);
        }

        if (studentPage == null) {
            studentPage = Page.empty();
        }

        List<StudentAdvisorView> content = studentPage.getContent();
        return new PaginatedResponse<>(
                content,
                pageNo,
                pageSize,
                studentPage.getTotalElements(),
                studentPage.getTotalPages(),
                studentPage.isLast()
        );
    }

    public List<String> getAllStudentIds(User loggedInUser) {
        log.debug("Logged in user = %s".formatted(loggedInUser));
        Set<Role> roles = userService.getRoles(loggedInUser);
        log.debug("Roles = %s".formatted(roles));
        if (!roles.contains(Role.ADVISOR)) {
            throw new RuntimeException("Unexpected error! report");
        }
        return studentRepository.findAllByAdvisorUserId(loggedInUser.getId());
    }

    @Transactional
    public Student addStudent(StudentRegistrationRequest studentRegistrationRequest, User loggedInUser) {
        if (userRepository.existsByEmail(studentRegistrationRequest.email())) {
            throw new DuplicateResourceException(
                    "email already taken"
            );
        }

        if (studentRegistrationRequest.id() != null) {
            if (studentRepository.existsById(studentRegistrationRequest.id())) {
                throw new DuplicateResourceException(
                        "student id already taken"
                );
            }
        }

        Advisor advisor = advisorService.getAdvisorByAdvisorId(studentRegistrationRequest.advisorId(), loggedInUser);

        User user = User.builder()
                .firstName(studentRegistrationRequest.firstName())
                .lastName(studentRegistrationRequest.lastName())
                .email(studentRegistrationRequest.email())
//                .password(passwordEncoder.encode(studentRegistrationRequest.password()))
                .phoneNumber(studentRegistrationRequest.phoneNumber())
                .picture(studentRegistrationRequest.picture())
                .role(Role.STUDENT)
                .build();
        User savedUser = userRepository.save(user);

        return studentRepository.save(
                Student.builder()
                        .id(studentRegistrationRequest.id())
                        .advisor(advisor)
                        .program(studentRegistrationRequest.program())
                        .year(studentRegistrationRequest.year())
                        .educationStatus(studentRegistrationRequest.educationStatus())
                        .user(savedUser)
                        .build()
        );
    }


    @Transactional
    public CsvUploadResponse uploadStudents(MultipartFile file, User loggedInUser) throws IOException {
        Instant startParseCsv = Instant.now();
        Set<StudentCsvRepresentation> studentCsvRepresentationSet = parseCsv(file);
        Instant endParseCsv = Instant.now();

        Instant startPartition = Instant.now();
        Map<Boolean, List<StudentCsvRepresentation>> partitionedStudents = studentCsvRepresentationSet
                .stream()
                .collect(
                        Collectors.partitioningBy(
                                s -> {
                                    if (userRepository.existsByEmail(s.getEmail())) {
                                        return false;
                                    }
                                    if (studentRepository.existsById(s.getStudentId())) {
                                        return false;
                                    }
                                    return s.getAdvisorId() == null || s.getAdvisorId().isBlank()
                                           || advisorRepository.existsById(s.getAdvisorId());
                                }
                        )
                );
        log.debug("partitionedStudents = %s".formatted(partitionedStudents));
        Instant endPartition = Instant.now();

        Instant startFilter = Instant.now();
        Set<StudentCsvRepresentation> filteredStudents = new HashSet<>(partitionedStudents.get(true));
        Instant endFilter = Instant.now();

        Instant startWrongInformation = Instant.now();
        Set<String> studentsWithWrongInformation = partitionedStudents.get(false)
                .stream()
                .map(StudentCsvRepresentation::getStudentId)
                .collect(Collectors.toSet());
        Instant endWrongInformation = Instant.now();

        Instant startBuildList = Instant.now();
        List<Student> students = filteredStudents
                .parallelStream()
                .map(csv -> {
                    Advisor advisor = null;
                    User user = User.builder()
                            .firstName(csv.getFirstName())
                            .lastName(csv.getLastName())
                            .email(csv.getEmail())
                            .password(passwordEncoder.encode(csv.getPassword()))
                            .phoneNumber(csv.getPhoneNumber())
                            .role(Role.STUDENT)
                            .picture(csv.getPicture())
                            .isEnabled(csv.isEnabled())
                            .isCredentialsNonExpired(true)
                            .build();

                    if (!csv.getAdvisorId().isBlank()) {
                        advisor = advisorService.getAdvisorByAdvisorId(csv.getAdvisorId(), loggedInUser);
                    }

                    return Student.builder()
                            .id(csv.getStudentId())
                            .advisor(advisor)
                            .program(csv.getProgram())
                            .year(csv.getYear())
                            .version(0)
                            .educationStatus(csv.getEducationStatus())
                            .user(user)
                            .build();
                }).toList();
        List<User> users = students.stream().map(Student::getUser).toList();
        Instant endBuildList = Instant.now();

        Instant startSaveUsers = Instant.now();
        List<User> savedUsers = userRepository.saveAll(users);
        Instant endSaveUsers = Instant.now();

        if (savedUsers.isEmpty()) {
            throw new APIException(HttpStatus.INTERNAL_SERVER_ERROR, "failed to save users");
        }

        Instant startSaveStudents = Instant.now();
        List<Student> savedStudents = studentRepository.saveAll(students);
        Instant endSaveStudents = Instant.now();

        log.debug("Time taken to parse csv: {}", Duration.between(startParseCsv, endParseCsv).toMillis());
        log.debug("Time taken to partition: {}", Duration.between(startPartition, endPartition).toMillis());
        log.debug("Time taken to filter: {}", Duration.between(startFilter, endFilter).toMillis());
        log.debug("Time taken to wrong information: {}", Duration.between(startWrongInformation, endWrongInformation).toMillis());
        log.debug("Time taken to build student list: {}", Duration.between(startBuildList, endBuildList).toMillis());
        log.debug("Time taken to save users list: {}", Duration.between(startSaveUsers, endSaveUsers).toMillis());
        log.debug("Time taken to save student list: {}", Duration.between(startSaveStudents, endSaveStudents).toMillis());
        return CsvUploadResponse.builder()
                .successCount(savedStudents.size())
                .failedCount(studentsWithWrongInformation.size())
                .failedReason(studentsWithWrongInformation.isEmpty()
                        ? null
                        : "Failed to add students with IDs: " + studentsWithWrongInformation
                )
                .build();
    }

    @Transactional
    public Student updateStudent(String studentId, StudentUpdateDto updateDto, User loggedInUser) {
        Student student = studentRepository.lockStudentById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "student with id `%s` not found".formatted(studentId)
                ));
        if (!loggedInUser.isAdmin()) {
            updateDto.setAdvisorId(null);
        }
        studentMapper.partialUpdate(updateDto, student);
        if (updateDto.getAdvisorId() != null) {
            Advisor advisor = advisorService.getAdvisorByAdvisorId(updateDto.getAdvisorId(), loggedInUser);
            student.setAdvisor(advisor);
        }
        studentRepository.save(student);
        return student;
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
