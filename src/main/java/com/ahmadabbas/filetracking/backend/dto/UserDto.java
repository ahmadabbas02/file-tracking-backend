package com.ahmadabbas.filetracking.backend.dto;

import com.ahmadabbas.filetracking.backend.entity.User;
import com.ahmadabbas.filetracking.backend.enums.Role;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Builder
@Getter
public class UserDto {
    private Integer id;
    private String loginId;
    private Role role;

    public static UserDto fromEntity(@NonNull User user) {
        return UserDto.builder()
                .id(user.getId())
                .loginId(user.getLoginId())
                .role(user.getRole())
                .build();
    }

    public static User toEntity(@NonNull UserDto userDto) {
        return User.builder()
                .id(userDto.getId())
                .loginId(userDto.getLoginId())
                .role(userDto.getRole())
                .build();
    }
}
