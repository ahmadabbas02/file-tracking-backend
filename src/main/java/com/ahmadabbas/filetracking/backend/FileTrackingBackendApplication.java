package com.ahmadabbas.filetracking.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FileTrackingBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(FileTrackingBackendApplication.class, args);
    }

//    @Bean
//    public CommandLineRunner commandLineRunner(
//            AuthenticationService authService
//    ) {
//        return args -> {
//            RegisterRequest adminReq = RegisterRequest.builder()
//                    .loginId("1")
//                    .password("admin")
//                    .role(Role.ADMINISTRATOR)
//                    .build();
//            authService.register(adminReq);
//
//            RegisterRequest secretaryReq = RegisterRequest.builder()
//                    .loginId("2")
//                    .password("secretary")
//                    .role(Role.SECRETARY)
//                    .build();
//            authService.register(secretaryReq);
//
//            RegisterRequest studentReq = RegisterRequest.builder()
//                    .loginId("20801142")
//                    .password("student")
//                    .role(Role.STUDENT)
//                    .build();
//            authService.register(studentReq);
//        };
//    }

}
