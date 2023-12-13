package com.ahmadabbas.filetracking.backend.document.payload;

import com.ahmadabbas.filetracking.backend.document.base.DocumentDto;

import java.util.List;

public record DocumentResponse(
        List<DocumentDto> content,
        int pageNo,
        int pageSize,
        long totalElements,
        int totalPages,
        boolean isLastPage
) {
}
