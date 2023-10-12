package com.ahmadabbas.filetracking.backend.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Builder
@Getter
public class ErrorDetails {

    private final Date timestamp;
    private final String message;
    private final String details;

}
