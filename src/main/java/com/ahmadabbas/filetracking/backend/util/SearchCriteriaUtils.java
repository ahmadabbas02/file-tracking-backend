package com.ahmadabbas.filetracking.backend.util;

import com.ahmadabbas.filetracking.backend.exception.APIException;
import com.blazebit.persistence.CriteriaBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

@Slf4j
public class SearchCriteriaUtils {

    public static <T> void addNameCriteria(CriteriaBuilder<T> criteriaBuilder,
                                           String name) {
        addNameCriteria(criteriaBuilder, name, "user.firstName", "user.lastName");
    }

    public static <T> void addNameCriteria(CriteriaBuilder<T> criteriaBuilder,
                                           String name,
                                           String firstNameExpression,
                                           String lastNameExpression) {
        log.debug("SearchCriteriaUtils.addNameCriteria");
        log.debug("name = {}, firstNameExpression = {}, lastNameExpression = {}",
                name, firstNameExpression, lastNameExpression);
        String[] names = name.split("\\s+");
        if (names.length > 2) {
            throw new APIException(HttpStatus.BAD_REQUEST, "Maximum of 2 words allowed in search!");
        }
        if (names.length == 2) {
            String firstName = "%" + names[0] + "%";
            String lastName = "%" + names[1] + "%";
            log.debug("fullName = {} {}", firstName, lastName);
            criteriaBuilder
                    .where(firstNameExpression).like(false).value(firstName).noEscape()
                    .where(lastNameExpression).like(false).value(lastName).noEscape();
        } else if (names.length == 1) {
            String searchQuery = "%" + names[0] + "%";
            criteriaBuilder.whereOr()
                    .where(firstNameExpression).like(false).value(searchQuery).noEscape()
                    .where(lastNameExpression).like(false).value(searchQuery).noEscape()
                    .endOr();
        }
    }
}
