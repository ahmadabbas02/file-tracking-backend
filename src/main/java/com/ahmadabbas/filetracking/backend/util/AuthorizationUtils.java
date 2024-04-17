package com.ahmadabbas.filetracking.backend.util;

import com.ahmadabbas.filetracking.backend.config.AuthorizationRule;
import com.ahmadabbas.filetracking.backend.user.Role;
import com.ahmadabbas.filetracking.backend.user.User;
import com.ahmadabbas.filetracking.backend.util.payload.EndpointAccess;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.ahmadabbas.filetracking.backend.user.Role.*;
import static org.springframework.http.HttpMethod.*;

public class AuthorizationUtils {

    public static final String[] WHITE_LIST_URL = {
            "/v3/api-docs",
            "/v3/api-docs/**",
            "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/ui",
            "/configuration/security",
            "/swagger-ui/**",
            "/webjars/**",
            "/swagger-ui.html",
            "/api/v1/auth/login",
            "/api/v1/auth/activate",
            "/api/v1/auth/activation-email",
    };

    public static List<AuthorizationRule> getAuthorizationRules() {
        return Arrays.asList(
                // Adding new students or advisor
                new AuthorizationRule(POST,
                        new Role[]{ADMINISTRATOR},
                        "api/v1/students",
                        "api/v1/students/upload",
                        "api/v1/advisors"),
                // Modifying/changing category of a document
                new AuthorizationRule(new Role[]{ADMINISTRATOR, SECRETARY}, "api/v1/documents/modify-category"),
                // Getting all advisors
                new AuthorizationRule(GET, new Role[]{ADMINISTRATOR, CHAIR, SECRETARY}, "api/v1/advisors"),
                // Getting all students
                new AuthorizationRule(GET, new Role[]{ADMINISTRATOR, CHAIR, SECRETARY, ADVISOR}, "api/v1/students"),
                // Only students can add these
                new AuthorizationRule(POST,
                        new Role[]{STUDENT},
                        "api/v1/documents/upload/contact",
                        "api/v1/documents/upload/petition",
                        "api/v1/documents/upload/medical-report"),
                // category creation
                new AuthorizationRule(POST, new Role[]{ADMINISTRATOR, CHAIR, SECRETARY}, "api/v1/categories"),
                // category deletion
                new AuthorizationRule(POST, new Role[]{ADMINISTRATOR}, "api/v1/categories/**"),
                // only secretary and admin can upload
                new AuthorizationRule(POST, new Role[]{ADMINISTRATOR, SECRETARY}, "api/v1/documents/upload"),
                // only admin can change category view permissions
                new AuthorizationRule(POST, new Role[]{ADMINISTRATOR}, "api/v1/categories/permissions/**"),
                // only admin or secretary can GET all category perms (used in admin panel)
                new AuthorizationRule(GET, new Role[]{ADMINISTRATOR, SECRETARY}, "api/v1/categories/permissions"),
                // only admin can hide documents
                new AuthorizationRule(new Role[]{ADMINISTRATOR}, "api/v1/documents/*/delete"),
                // only admin can access system users
                new AuthorizationRule(new Role[]{ADMINISTRATOR}, "api/v1/users", "api/v1/users/{*userId}"),
                // only admin and secretary can update students
                new AuthorizationRule(PATCH, new Role[]{ADMINISTRATOR, SECRETARY}, "api/v1/students/{studentId}"),
                // only secretary can approve documents
                new AuthorizationRule(new Role[]{SECRETARY}, "api/v1/documents/*/approve"),
                // all can get their endpoint access
                new AuthorizationRule(Role.values(), "api/v1/users/endpoints")
        );
    }

    public static List<EndpointAccess> getAvailableEndpoints(User user) {
        Set<Role> roles = user.getRoles();
        Role role = roles.iterator().next();
        return getAuthorizationRules().stream()
                .filter(rule -> Arrays.asList(rule.roles()).contains(role.name()))
                .flatMap(rule -> Arrays.stream(rule.endpoints())
                        .map(endpoint -> new EndpointAccess(endpoint, rule.method() != null ? rule.method().name() : "ANY")))
                .collect(Collectors.toList());
    }

}
