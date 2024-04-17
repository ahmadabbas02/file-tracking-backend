package com.ahmadabbas.filetracking.backend.config;

import com.ahmadabbas.filetracking.backend.user.Role;
import org.springframework.http.HttpMethod;

import java.util.Arrays;

public class AuthorizationRule {
    private final String[] endpoints;
    private HttpMethod method;
    private final Role[] roles;

    public AuthorizationRule(Role[] roles, String... endpoints) {
        this.roles = roles;
        this.endpoints = endpoints;
    }

    public AuthorizationRule(HttpMethod method, Role[] roles, String... endpoints) {
        this.method = method;
        this.roles = roles;
        this.endpoints = endpoints;
    }

    public String[] endpoints() {
        return endpoints;
    }

    public HttpMethod method() {
        return method;
    }

    public String[] roles() {
        return Arrays.stream(roles).map(Enum::name).toList().toArray(new String[0]);
    }
}