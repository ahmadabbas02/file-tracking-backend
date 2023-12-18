package com.ahmadabbas.filetracking.backend.util.payload;

import java.util.List;

public record PaginatedResponse<T>(
        List<T> results,
        int pageNo,
        int pageSize,
        long totalElements,
        int totalPages,
        boolean isLastPage
) {
}
