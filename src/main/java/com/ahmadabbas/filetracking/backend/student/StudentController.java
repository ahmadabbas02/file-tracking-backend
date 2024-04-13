package com.ahmadabbas.filetracking.backend.student;

import com.ahmadabbas.filetracking.backend.document.base.DocumentStatus;
import com.ahmadabbas.filetracking.backend.student.payload.StudentDto;
import com.ahmadabbas.filetracking.backend.student.payload.StudentMapper;
import com.ahmadabbas.filetracking.backend.student.payload.StudentRegistrationRequest;
import com.ahmadabbas.filetracking.backend.student.payload.StudentUpdateDto;
import com.ahmadabbas.filetracking.backend.student.views.StudentWithAdvisorView;
import com.ahmadabbas.filetracking.backend.user.User;
import com.ahmadabbas.filetracking.backend.util.payload.CsvUploadResponse;
import com.ahmadabbas.filetracking.backend.util.payload.PaginatedResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/students")
@Tag(name = "Student")
public class StudentController {
    private final StudentService studentService;
    private final StudentMapper studentMapper;

    @Operation(summary = "Student information", description = "Returns the student's personal information.")
    @GetMapping("/{studentId}")
    public ResponseEntity<StudentWithAdvisorView> getStudent(@PathVariable String studentId,
                                                             @AuthenticationPrincipal User loggedInUser) {
        StudentWithAdvisorView student = studentService.getStudentView(studentId, loggedInUser);
        return ResponseEntity.ok(student);
    }

    @Operation(
            summary = "Get all students",
            description = "Returns a pagination result of all students in the database sorted by default on id and ascending order."
    )
    @GetMapping("")
    public ResponseEntity<PaginatedResponse<StudentWithAdvisorView>> getAllStudents(
            @RequestParam(defaultValue = "1", required = false) int pageNo,
            @RequestParam(defaultValue = "10", required = false) int pageSize,
            @RequestParam(defaultValue = "id", required = false) String sortBy,
            @RequestParam(defaultValue = "asc", required = false) String order,
            @RequestParam(defaultValue = "", required = false) String searchQuery,
            @RequestParam(defaultValue = "", required = false) String advisorId,
            @RequestParam(defaultValue = "", required = false) List<String> programs,
            @RequestParam(defaultValue = "", required = false) List<DocumentStatus.InternshipCompletionStatus> completionStatuses,
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(
                studentService.getAllStudents(user,
                        pageNo,
                        pageSize,
                        sortBy,
                        order,
                        searchQuery,
                        advisorId,
                        programs,
                        completionStatuses)
        );
    }

    @Operation(summary = "Add new student", description = "Adds a new student to the database with the specified information.")
    @PostMapping("")
    public ResponseEntity<StudentWithAdvisorView> registerStudent(@RequestBody @Valid StudentRegistrationRequest studentRegistrationRequest,
                                                                  @AuthenticationPrincipal User loggedInUser) {
        StudentWithAdvisorView createdStudent = studentService.addStudent(studentRegistrationRequest, loggedInUser);
        return new ResponseEntity<>(createdStudent, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Batch add students",
            description = """
                    Adds the students in the csv to the database with their specified IDs.
                    The csv should have the following format: `studentId,advisorId,name,surname,email,password,phoneNumber,program,year,picture,isEnabled` as headers."""
    )
    @PostMapping(value = "/upload", consumes = {"multipart/form-data"})
    public ResponseEntity<CsvUploadResponse> uploadStudents(@RequestPart("file") MultipartFile file,
                                                            @AuthenticationPrincipal User loggedInUser) throws IOException {
        return new ResponseEntity<>(studentService.uploadStudents(file, loggedInUser), HttpStatus.CREATED);
    }

    @Operation(
            summary = "Update student",
            description = "Update student details"
    )
    @PatchMapping("/{studentId}")
    public ResponseEntity<StudentDto> updateStudent(
            @PathVariable String studentId,
            @RequestBody @Valid StudentUpdateDto updateDto,
            @AuthenticationPrincipal User loggedInUser
    ) {
        Student student = studentService.updateStudent(studentId, updateDto, loggedInUser);
        return ResponseEntity.ok(studentMapper.toDto(student));
    }
}
