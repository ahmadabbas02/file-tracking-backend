package com.ahmadabbas.filetracking.backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/protected")
public class DummyController {
    @GetMapping
    public ResponseEntity<String> dummyEndpoint() {
        return ResponseEntity.ok("Access successful!");
    }

}
