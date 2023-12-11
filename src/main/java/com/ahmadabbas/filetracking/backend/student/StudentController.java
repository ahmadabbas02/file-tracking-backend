package com.ahmadabbas.filetracking.backend.student;

import com.ahmadabbas.filetracking.backend.student.payload.StudentRegistrationRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/student")
public class StudentController {
    private final StudentService studentService;
    private final StudentDtoMapper studentDtoMapper;

    public StudentController(StudentService studentService, StudentDtoMapper studentDtoMapper) {
        this.studentService = studentService;
        this.studentDtoMapper = studentDtoMapper;
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
