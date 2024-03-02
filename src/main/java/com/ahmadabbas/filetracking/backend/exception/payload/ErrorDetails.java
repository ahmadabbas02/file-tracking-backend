package com.ahmadabbas.filetracking.backend.exception.payload;

import java.time.LocalDateTime;
import java.time.ZoneId;

public record ErrorDetails(LocalDateTime timestamp, String message, String details) {
    public ErrorDetails {
        if (timestamp == null){
            timestamp = LocalDateTime.now(ZoneId.of("Europe/Athens"));
        }
    }
}
