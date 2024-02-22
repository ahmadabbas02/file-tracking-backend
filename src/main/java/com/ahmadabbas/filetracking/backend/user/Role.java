package com.ahmadabbas.filetracking.backend.user;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.List;


public enum Role {
    STUDENT,
    ADVISOR,
    SECRETARY,
    CHAIR,
    VICE_CHAR,
    ADMINISTRATOR;

    public List<SimpleGrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
        return authorities;
    }

}
