package com.ahmadabbas.filetracking.backend.document.base;

import com.ahmadabbas.filetracking.backend.student.StudentDtoMapper;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class DocumentDtoMapper implements Function<Document, DocumentDto> {
    private final StudentDtoMapper studentDtoMapper;

    public DocumentDtoMapper(StudentDtoMapper studentDtoMapper) {
        this.studentDtoMapper = studentDtoMapper;
    }

    @Override
    public DocumentDto apply(Document document) {
        return new DocumentDto(
                document.getId(),
                document.getTitle(),
                document.getPath(),
                document.getCategory().getParentCategoryId(),
                document.getCategory().getCategoryId(),
                document.getCategory().getName(),
                studentDtoMapper.apply(document.getStudent()),
                document.getUploadedAt()
        );
    }
}
