package com.ahmadabbas.filetracking.backend.exception.payload;

import java.util.Date;

public record ErrorDetails(Date timestamp, String message, String details) {
}
