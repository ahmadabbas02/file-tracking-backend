package com.ahmadabbas.filetracking.backend.repository;

import com.ahmadabbas.filetracking.backend.entity.User;
import com.ahmadabbas.filetracking.backend.enums.Role;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Arrays;
import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class UserRepositoryTests {
    @Autowired
    private UserRepository userRepository;

    @Test
    public void UserRepository_SaveUser_ReturnsSavedUser() {
        User user = User.builder()
                .loginId("1")
                .password("password1")
                .role(Role.ADMINISTRATOR)
                .build();

        User savedUser = userRepository.save(user);

        Assertions.assertThat(savedUser).isNotNull();
        Assertions.assertThat(savedUser.getId()).isGreaterThan(0);
    }

    @Test
    public void UserRepository_GetAll_ReturnsMoreThanOneUser() {
        userRepository.saveAll(
                Arrays.asList(
                        User.builder().role(Role.ADMINISTRATOR).loginId("1").password("password1").build(),
                        User.builder().role(Role.STUDENT).loginId("2").password("password2").build()
                )
        );

        List<User> userList = userRepository.findAll();

        Assertions.assertThat(userList).isNotNull();
        Assertions.assertThat(userList.size()).isEqualTo(2);
    }

    @Test
    public void UserRepository_GetAllNonAdmin_ReturnsOneUser() {
        userRepository.saveAll(
                Arrays.asList(
                        User.builder().role(Role.ADMINISTRATOR).loginId("1").password("password1").build(),
                        User.builder().role(Role.STUDENT).loginId("2").password("password2").build(),
                        User.builder().role(Role.SECRETARY).loginId("3").password("password3").build(),
                        User.builder().role(Role.CHAIR).loginId("4").password("password4").build()
                )
        );

        List<User> userList = userRepository.findAllNonAdminUsers().orElseThrow();

        Assertions.assertThat(userList).isNotNull();
        Assertions.assertThat(userList.size()).isEqualTo(3);
    }

    @Test
    public void UserRepository_FindByLoginId_ReturnUser() {
        userRepository.save(
                User.builder().role(Role.ADMINISTRATOR).loginId("1").password("password1").build()
        );

        User user = userRepository.findByLoginId("1").orElseThrow();

        Assertions.assertThat(user).isNotNull();
        Assertions.assertThat(user.getLoginId()).isEqualTo("1");
    }
}
