package com.ahmadabbas.filetracking.backend.student;

import com.ahmadabbas.filetracking.backend.student.payload.StudentRegistrationRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/students")
public class StudentController {
    private final StudentService studentService;
    private final StudentDtoMapper studentDtoMapper;

    public StudentController(StudentService studentService, StudentDtoMapper studentDtoMapper) {
        this.studentService = studentService;
        this.studentDtoMapper = studentDtoMapper;
    }

    @GetMapping("{studentId}")
    public ResponseEntity<StudentDto> getStudent(@PathVariable String studentId) {
        Student student = studentService.getStudent(studentId);
        return ResponseEntity.ok(studentDtoMapper.apply(student));
    }

    @PostMapping
    public ResponseEntity<StudentDto> registerStudent(@RequestBody StudentRegistrationRequest studentRegistrationRequest) {
        Student createdStudent = studentService.addStudent(studentRegistrationRequest);
        return new ResponseEntity<>(studentDtoMapper.apply(createdStudent), HttpStatus.CREATED);
    }

//    @GetMapping
//    public ResponseEntity<List<Student>> getStudents() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        User user = (User) authentication.getPrincipal();
//        System.out.println(authentication.getDetails());
//        System.out.println(authentication.getAuthorities());
//        System.out.println(authentication.getCredentials());
//
//        return ResponseEntity.ok(studentRepository.findAllByAdvisor_User_Id(user.getId()));
//    }
}
