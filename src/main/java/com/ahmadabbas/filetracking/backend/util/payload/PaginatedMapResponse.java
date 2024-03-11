package com.ahmadabbas.filetracking.backend.util.payload;

import java.util.Map;

public record PaginatedMapResponse<K, V>(
        Map<K, V> results,
        int pageNo,
        int pageSize,
        long totalElements,
        int totalPages,
        boolean isLastPage
) {
}