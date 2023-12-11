package com.ahmadabbas.filetracking.backend.student;

import com.ahmadabbas.filetracking.backend.advisor.AdvisorDtoMapper;
import com.ahmadabbas.filetracking.backend.user.UserDtoMapper;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class StudentDtoMapper implements Function<Student, StudentDto> {

    private final AdvisorDtoMapper advisorDtoMapper;
    private final UserDtoMapper userDtoMapper;

    public StudentDtoMapper(AdvisorDtoMapper advisorDtoMapper, UserDtoMapper userDtoMapper) {
        this.advisorDtoMapper = advisorDtoMapper;
        this.userDtoMapper = userDtoMapper;
    }

    @Override
    public StudentDto apply(Student student) {
        return new StudentDto(
                student.getId(),
                student.getDepartment(),
                student.getYear(),
                student.getPicture(),
                userDtoMapper.apply(student.getUser()),
                advisorDtoMapper.apply(student.getAdvisor()),
                student.getCreatedAt()
        );
    }
}
