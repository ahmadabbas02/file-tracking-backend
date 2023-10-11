package com.ahmadabbas.filetracking.backend.dto;

import com.ahmadabbas.filetracking.backend.entity.User;
import com.ahmadabbas.filetracking.backend.enums.Role;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Builder
@Getter
public class UserDto implements DtoEntityMapper<User> {
    private Integer id;
    private String loginId;
    private Role role;

    @Override
    public User toEntity() {
        return User.builder()
                .id(getId())
                .loginId(getLoginId())
                .role(getRole())
                .build();
    }
}
