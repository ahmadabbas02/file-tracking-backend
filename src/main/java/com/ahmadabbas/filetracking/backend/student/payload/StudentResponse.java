package com.ahmadabbas.filetracking.backend.student.payload;

import com.ahmadabbas.filetracking.backend.student.StudentDto;

import java.util.List;

public record StudentResponse(
        List<StudentDto> content,
        int pageNo,
        int pageSize,
        long totalElements,
        int totalPages,
        boolean isLastPage
) {
}
