package com.ahmadabbas.filetracking.backend.repository;

import com.ahmadabbas.filetracking.backend.user.Role;
import com.ahmadabbas.filetracking.backend.user.User;
import com.ahmadabbas.filetracking.backend.user.UserRepository;
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

//    @Test
//    public void UserRepository_SaveUser_ReturnsSavedUser() {
//        User user = User.builder()
//                .id(1L)
//                .password("password1")
//                .role(Role.ADMINISTRATOR)
//                .build();
//
//        User savedUser = userRepository.save(user);
//
//        Assertions.assertThat(savedUser).isNotNull();
//        Assertions.assertThat(savedUser.getId()).isGreaterThan(0);
//    }
//
//    @Test
//    public void UserRepository_GetAll_ReturnsMoreThanOneUser() {
//        userRepository.saveAll(
//                Arrays.asList(
//                        User.builder().role(Role.ADMINISTRATOR).id(1L).password("password1").build(),
//                        User.builder().role(Role.STUDENT).id(1L).password("password2").build()
//                )
//        );
//
//        List<User> userList = userRepository.findAll();
//
//        Assertions.assertThat(userList).isNotNull();
//        Assertions.assertThat(userList.size()).isEqualTo(2);
//    }
//
//    @Test
//    public void UserRepository_GetAllNonAdmin_ReturnsOneUser() {
//        userRepository.saveAll(
//                Arrays.asList(
//                        User.builder().role(Role.ADMINISTRATOR).id(1L).password("password1").build(),
//                        User.builder().role(Role.STUDENT).id(2L).password("password2").build(),
//                        User.builder().role(Role.SECRETARY).id(3L).password("password3").build(),
//                        User.builder().role(Role.CHAIR).id(4L).password("password4").build()
//                )
//        );
//
//        List<User> userList = userRepository.findAllNonAdminUsers();
//        Assertions.assertThat(userList).isNotNull();
//        Assertions.assertThat(userList.size()).isEqualTo(3);
//    }
//
//    @Test
//    public void UserRepository_FindByLoginId_ReturnUser() {
//        userRepository.save(
//                User.builder().role(Role.ADMINISTRATOR).id(1L).password("password1").build()
//        );
//
//        User user = userRepository.findById(1L).orElseThrow();
//
//        Assertions.assertThat(user).isNotNull();
//        Assertions.assertThat(user.getId()).isEqualTo(1L);
//    }
}
