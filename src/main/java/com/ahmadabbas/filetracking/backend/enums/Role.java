package com.ahmadabbas.filetracking.backend.enums;

import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.List;

@Getter
public enum Role {
    STUDENT,
    SECRETARY,
    CHAIR,
    VICE_CHAR,
    ADMINISTRATOR;

    public List<SimpleGrantedAuthority> getAuthorities() {
        var authorities = new ArrayList<SimpleGrantedAuthority>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
        return authorities;
    }

}
