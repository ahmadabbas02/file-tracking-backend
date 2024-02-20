package com.ahmadabbas.filetracking.backend.student;

import com.ahmadabbas.filetracking.backend.advisor.Advisor;
import com.ahmadabbas.filetracking.backend.advisor.AdvisorRepository;
import com.ahmadabbas.filetracking.backend.advisor.AdvisorService;
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
import org.springframework.security.core.Authentication;
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
    private final StudentDao studentDao;
    private final UserRepository userRepository;
    private final AdvisorRepository advisorRepository;
    private final AdvisorService advisorService;
    private final UserService userService;

//    private final PageableUtil pageableUtil;

    private final PasswordEncoder passwordEncoder;

    public Student getStudent(String id) {
        return studentDao.getStudent(id);
    }

    public PaginatedResponse<StudentDto> getAllStudents(Authentication authentication,
                                                        int pageNo, int pageSize, String sortBy, String order,
                                                        String searchQuery) {
        Pageable pageable = PageableUtil.getPageable(pageNo, pageSize, sortBy, order);
        Page<Student> studentPage = null;
        // TODO: check won't be needed later after forcing all users to be logged in
        if (authentication != null && authentication.isAuthenticated()) {
            User user = (User) authentication.getPrincipal();
            log.info("Logged in user = %s".formatted(user));
            Set<Role> roles = userService.getRoles(user);
            log.info("Roles = %s".formatted(roles));
            if (roles.contains(Role.ADVISOR)) {
                if (searchQuery.isEmpty()) {
                    log.info("No search query provided, getting all students..");
                    studentPage = studentDao.getAllStudentsByAdvisorUserId(user.getId(), pageable);
                } else {
                    // TODO: Decide how we want searching to be done
                    log.info("Provided search query: '%s', getting all students..".formatted(searchQuery));
                    studentPage = StringUtils.isNumeric(searchQuery)
                            ? studentDao.getAllStudentsByIdAndAdvisor(false, searchQuery, user.getId(), pageable)
                            : studentDao.getAllStudentsByNameAndAdvisor(true, searchQuery, user.getId(), pageable);
                }
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
                        : studentDao.getAllStudentsById(true, searchQuery, pageable);
            }
        }

        if (studentPage == null) {
            studentPage = Page.empty();
        }

        List<StudentDto> content = studentPage.getContent()
                .stream()
                .map(StudentMapper.INSTANCE::toDto)
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
        return studentDao.save(student);
    }

    @Transactional
    public CsvUploadResponse uploadStudents(MultipartFile file) throws IOException {
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
        Instant endBuildList = Instant.now();

        Instant startSaveStudents = Instant.now();
        List<Student> savedStudents = studentDao.saveAll(students);
        Instant endSaveStudents = Instant.now();

        log.info("Time taken to parse csv: " + Duration.between(startParseCsv, endParseCsv).toMillis());
        log.info("Time taken to partition: " + Duration.between(startPartition, endPartition).toMillis());
        log.info("Time taken to filter: " + Duration.between(startFilter, endFilter).toMillis());
        log.info("Time taken to wrong information: " + Duration.between(startWrongInformation, endWrongInformation).toMillis());
        log.info("Time taken to build student list: " + Duration.between(startBuildList, endBuildList).toMillis());
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
