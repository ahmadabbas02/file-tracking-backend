package com.ahmadabbas.filetracking.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class FileTrackingBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(FileTrackingBackendApplication.class, args);
    }

}
