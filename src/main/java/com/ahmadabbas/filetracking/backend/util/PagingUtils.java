package com.ahmadabbas.filetracking.backend.util;

import com.blazebit.persistence.PagedList;
import com.blazebit.persistence.PaginatedCriteriaBuilder;
import org.springframework.data.domain.*;

import java.util.Map;
import java.util.stream.Collectors;

public class PagingUtils {
    public static Pageable getPageable(int pageNo, int pageSize, String sortBy, String order) {
        if (pageNo - 1 < 0) pageNo = 0;
        Sort sort = order.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        return PageRequest.of(pageNo - 1, pageSize, sort);
    }

    public static <T> void applySorting(Pageable pageable, PaginatedCriteriaBuilder<T> criteriaBuilder) {
        Map<String, Sort.Direction> sortProperties = pageable.getSort()
                .stream()
                .collect(Collectors.toMap(Sort.Order::getProperty, Sort.Order::getDirection));
        for (var entry : sortProperties.entrySet()) {
            criteriaBuilder.orderBy(entry.getKey(), entry.getValue().equals(Sort.Direction.ASC));
        }
        if (!sortProperties.containsKey("id"))
            criteriaBuilder.orderBy("id", true);
    }

    public static <T> Page<T> getOrderedPage(PaginatedCriteriaBuilder<T> criteriaBuilder,
                                             Pageable pageable) {
        PagingUtils.applySorting(pageable, criteriaBuilder);
        PagedList<T> resultList = criteriaBuilder.getResultList();
        return new PageImpl<>(resultList, pageable, resultList.getTotalSize());
    }
}
