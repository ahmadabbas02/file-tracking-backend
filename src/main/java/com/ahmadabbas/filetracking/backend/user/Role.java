package com.ahmadabbas.filetracking.backend.user;

import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.List;

@Getter
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
