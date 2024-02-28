package com.ahmadabbas.filetracking.backend.student;

import com.ahmadabbas.filetracking.backend.student.payload.StudentDto;
import com.ahmadabbas.filetracking.backend.student.payload.StudentMapper;
import com.ahmadabbas.filetracking.backend.student.payload.StudentRegistrationRequest;
import com.ahmadabbas.filetracking.backend.util.payload.CsvUploadResponse;
import com.ahmadabbas.filetracking.backend.util.payload.PaginatedResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/students")
public class StudentController {
    private final StudentService studentService;
    private final StudentMapper studentMapper;

    @Operation(summary = "Student information", description = "Returns the student's personal information.")
    @GetMapping("{studentId}")
    public ResponseEntity<StudentDto> getStudent(@PathVariable String studentId) {
        Student student = studentService.getStudent(studentId);
        return ResponseEntity.ok(studentMapper.toDto(student));
    }

    @Operation(
            summary = "Get all students",
            description = "Returns a pagination result of all students in the database sorted by default on id and ascending order."
    )
    @GetMapping
    public ResponseEntity<PaginatedResponse<StudentDto>> getAllStudents(
            @RequestParam(defaultValue = "1", required = false) int pageNo,
            @RequestParam(defaultValue = "10", required = false) int pageSize,
            @RequestParam(defaultValue = "id", required = false) String sortBy,
            @RequestParam(defaultValue = "asc", required = false) String order,
            @RequestParam(defaultValue = "", required = false) String searchQuery,
            Authentication authentication
    ) {
        return ResponseEntity.ok(studentService.getAllStudents(authentication, pageNo, pageSize, sortBy, order, searchQuery));
    }

    @Operation(summary = "Add new student", description = "Adds a new student to the database with the specified information.")
    @PostMapping
    public ResponseEntity<StudentDto> registerStudent(@RequestBody StudentRegistrationRequest studentRegistrationRequest) {
        Student createdStudent = studentService.addStudent(studentRegistrationRequest);
        StudentDto dto = studentMapper.toDto(createdStudent);
        return new ResponseEntity<>(dto, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Batch add students",
            description = "Adds the students in the csv to the database with their specified IDs.\nThe csv should have the following format: `studentId,advisorId,name,email,password,department,year,picture,isEnabled` as headers."
    )
    @PostMapping(value = "/upload", consumes = {"multipart/form-data"})
    public ResponseEntity<CsvUploadResponse> uploadStudents(@RequestPart("file") MultipartFile file) throws IOException {
        return new ResponseEntity<>(studentService.uploadStudents(file), HttpStatus.CREATED);
    }
}
