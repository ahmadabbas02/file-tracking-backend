package com.ahmadabbas.filetracking.backend.user;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;


public enum Role {
    STUDENT,
    ADVISOR,
    SECRETARY,
    CHAIR,
    VICE_CHAR,
    ADMINISTRATOR;

    public List<SimpleGrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + this.name()));
    }

}
