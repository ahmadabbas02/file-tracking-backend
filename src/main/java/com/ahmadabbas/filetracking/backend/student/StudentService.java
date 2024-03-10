package com.ahmadabbas.filetracking.backend.student;

import com.ahmadabbas.filetracking.backend.advisor.Advisor;
import com.ahmadabbas.filetracking.backend.advisor.AdvisorRepository;
import com.ahmadabbas.filetracking.backend.advisor.AdvisorService;
import com.ahmadabbas.filetracking.backend.exception.APIException;
import com.ahmadabbas.filetracking.backend.exception.DuplicateResourceException;
import com.ahmadabbas.filetracking.backend.student.payload.StudentCsvRepresentation;
import com.ahmadabbas.filetracking.backend.student.payload.StudentDto;
import com.ahmadabbas.filetracking.backend.student.payload.StudentMapper;
import com.ahmadabbas.filetracking.backend.student.payload.StudentRegistrationRequest;
import com.ahmadabbas.filetracking.backend.user.Role;
import com.ahmadabbas.filetracking.backend.user.User;
import com.ahmadabbas.filetracking.backend.user.UserRepository;
import com.ahmadabbas.filetracking.backend.user.UserService;
import com.ahmadabbas.filetracking.backend.util.PageableUtil;
import com.ahmadabbas.filetracking.backend.util.payload.CsvUploadResponse;
import com.ahmadabbas.filetracking.backend.util.payload.PaginatedResponse;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
    private final StudentDao studentDao;
    private final UserRepository userRepository;
    private final AdvisorRepository advisorRepository;
    private final AdvisorService advisorService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public Student getStudent(String id, User loggedInUser) {
        if (loggedInUser.getRoles().contains(Role.STUDENT)) {
            Student student = getStudentByUserId(loggedInUser.getId());
            if (!id.equals(student.getId())) {
                throw new AccessDeniedException("not authorized, you can only get details about your own profile");
            }
        } else if (loggedInUser.getRoles().contains(Role.ADVISOR)) {
            Student student = studentDao.getStudent(id);
            if (student.getAdvisor().getUser().getId().equals(loggedInUser.getId())) {
                return student;
            } else {
                throw new AccessDeniedException("not authorized, you can only get details about your own students");
            }
        }
        return studentDao.getStudent(id);
    }

    public Student getStudentByUserId(Long userId) {
        return studentDao.getStudentByUserId(userId);
    }

    public PaginatedResponse<StudentDto> getAllStudents(User loggedInUser,
                                                        int pageNo, int pageSize, String sortBy, String order,
                                                        String searchQuery) {
        Pageable pageable = PageableUtil.getPageable(pageNo, pageSize, sortBy, order);
        Page<Student> studentPage;
        log.info("Logged in user = %s".formatted(loggedInUser));
        Set<Role> roles = userService.getRoles(loggedInUser);
        log.info("Roles = %s".formatted(roles));
        if (roles.contains(Role.ADVISOR)) {
            if (searchQuery.isEmpty()) {
                log.info("No search query provided, getting all students..");
                studentPage = studentDao.getAllStudentsByAdvisorUserId(loggedInUser.getId(), pageable);
            } else {
                // TODO: Decide how we want searching to be done
                log.info("Provided search query: '%s', getting all students..".formatted(searchQuery));
                studentPage = StringUtils.isNumeric(searchQuery)
                        ? studentDao.getAllStudentsByIdAndAdvisor(false, searchQuery, loggedInUser.getId(), pageable)
                        : studentDao.getAllStudentsByNameAndAdvisor(true, searchQuery, loggedInUser.getId(), pageable);
            }
        } else {
            if (searchQuery.isEmpty()) {
                log.info("No search query provided, getting all students..");
                studentPage = studentDao.getAllStudents(pageable);
            } else {
                // TODO: Decide how we want searching to be done
                log.info("Provided search query: '%s', getting all students..".formatted(searchQuery));
                studentPage = StringUtils.isNumeric(searchQuery)
                        ? studentDao.getAllStudentsById(false, searchQuery, pageable)
                        : studentDao.getAllStudentsByName(true, searchQuery, pageable);
            }
        }

        if (studentPage == null) {
            studentPage = Page.empty();
        }

        List<StudentDto> content = studentPage.getContent()
                .stream()
                .map(studentMapper::toDto)
                .toList();
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
        log.info("Logged in user = %s".formatted(loggedInUser));
        Set<Role> roles = userService.getRoles(loggedInUser);
        log.info("Roles = %s".formatted(roles));
        if (!roles.contains(Role.ADVISOR)) {
            throw new RuntimeException("Unexpected error! report");
        }
        return studentDao.getAllStudentIdsByAdvisorUserId(loggedInUser.getId());
    }

    @Transactional
    public Student addStudent(StudentRegistrationRequest studentRegistrationRequest, User loggedInUser) {
        if (userRepository.existsByEmail(studentRegistrationRequest.email())) {
            throw new DuplicateResourceException(
                    "email already taken"
            );
        }

        Advisor advisor = advisorService.getAdvisorByAdvisorId(studentRegistrationRequest.advisorId(), loggedInUser);

        User user = User.builder()
                .name(studentRegistrationRequest.name())
                .email(studentRegistrationRequest.email())
                .password(passwordEncoder.encode(studentRegistrationRequest.password()))
                .picture(studentRegistrationRequest.picture())
                .role(Role.STUDENT)
                .build();
        User savedUser = userRepository.save(user);

        Student student = Student.builder()
                .advisor(advisor)
                .program(studentRegistrationRequest.program())
                .year(studentRegistrationRequest.year())
                .user(savedUser)
                .build();
        return studentDao.save(student);
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
                                    return s.getAdvisorId() == null || s.getAdvisorId().isBlank()
                                            || advisorRepository.existsById(s.getAdvisorId());
                                }
                        )
                );
        log.info("partitionedStudents = %s".formatted(partitionedStudents));
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
                .map(s -> {
                    Advisor advisor = null;
                    User user = User.builder()
                            .name(s.getName())
                            .email(s.getEmail())
                            .password(passwordEncoder.encode(s.getPassword()))
                            .role(Role.STUDENT)
                            .picture(s.getPicture())
                            .isEnabled(s.isEnabled())
                            .build();
                    if (!s.getAdvisorId().isBlank()) {
                        advisor = advisorService.getAdvisorByAdvisorId(s.getAdvisorId(), loggedInUser);
                    }

                    return Student.builder()
                            .id(s.getStudentId())
                            .advisor(advisor)
                            .program(s.getDepartment())
                            .year(s.getYear())
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
        List<Student> savedStudents = studentDao.saveAll(students);
        Instant endSaveStudents = Instant.now();

        log.info("Time taken to parse csv: " + Duration.between(startParseCsv, endParseCsv).toMillis());
        log.info("Time taken to partition: " + Duration.between(startPartition, endPartition).toMillis());
        log.info("Time taken to filter: " + Duration.between(startFilter, endFilter).toMillis());
        log.info("Time taken to wrong information: " + Duration.between(startWrongInformation, endWrongInformation).toMillis());
        log.info("Time taken to build student list: " + Duration.between(startBuildList, endBuildList).toMillis());
        log.info("Time taken to save users list: " + Duration.between(startSaveUsers, endSaveUsers).toMillis());
        log.info("Time taken to save student list: " + Duration.between(startSaveStudents, endSaveStudents).toMillis());
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
