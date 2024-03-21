package com.ahmadabbas.filetracking.backend.user;

import com.ahmadabbas.filetracking.backend.user.payload.UserDto;
import com.ahmadabbas.filetracking.backend.user.payload.UserMapper;
import com.ahmadabbas.filetracking.backend.user.payload.UserUpdateDto;
import com.ahmadabbas.filetracking.backend.util.payload.PaginatedResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @Operation(
            summary = "Get all users",
            description = "Returns a pagination result of all students in the database sorted by default on id and " +
                          "ascending order."
    )
    @GetMapping
    public ResponseEntity<PaginatedResponse<UserDto>> getAllUsers(
            @RequestParam(defaultValue = "1", required = false) int pageNo,
            @RequestParam(defaultValue = "10", required = false) int pageSize,
            @RequestParam(defaultValue = "id", required = false) String sortBy,
            @RequestParam(defaultValue = "asc", required = false) String order,
            @RequestParam(defaultValue = "", required = false) String name,
            @RequestParam(defaultValue = "", required = false) String roleId,
            @RequestParam(defaultValue = "", required = false) List<Role> roles,
            @AuthenticationPrincipal User loggedInUser
    ) {
        return ResponseEntity.ok(
                userService.getAllUsers(pageNo, pageSize, sortBy, order, name, roleId, roles)
        );
    }

    @Operation(
            summary = "Update user",
            description = "Update user partially"
    )
    @PatchMapping("{userId}")
    public ResponseEntity<UserDto> updateUser(
            @PathVariable Long userId,
            @RequestBody UserUpdateDto updateDto,
            @AuthenticationPrincipal User loggedInUser
    ) {
        User user = userService.updateUser(userId, updateDto);
        return ResponseEntity.ok(userMapper.toDto(user));
    }
}
