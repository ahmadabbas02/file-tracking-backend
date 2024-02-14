package com.ahmadabbas.filetracking.backend.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PageableUtil {
    public static Pageable getPageable(int pageNo, int pageSize, String sortBy, String order) {
        if (pageNo - 1 < 0) pageNo = 0;
        Sort sort = order.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        return PageRequest.of(pageNo - 1, pageSize, sort);
    }
}
