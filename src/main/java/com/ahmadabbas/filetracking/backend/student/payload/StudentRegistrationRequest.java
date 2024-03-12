package com.ahmadabbas.filetracking.backend.student.payload;

import com.ahmadabbas.filetracking.backend.exception.APIException;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;

public record StudentRegistrationRequest(
        @NotEmpty(message = "Student name should not be empty") String name,
        @NotEmpty(message = "Student surname should not be empty") String surname,
        @Email(message = "Student email should be valid") String email,
        @NotEmpty(message = "Student name should not be empty") String password,
        @NotEmpty(message = "Student program should not be empty") String program,
        @Positive @Min(1) Short year,
        @NotEmpty(message = "Student picture should not be empty") String picture,
        @NotEmpty(message = "Student's advisor id should not be empty") String advisorId
) {
    public StudentRegistrationRequest {
        List<String> allowedProgramValues = Arrays.asList("CMSE", "CMPE", "BLGM");
        if (!allowedProgramValues.contains(program)) {
            throw new APIException(HttpStatus.BAD_REQUEST,
                    "Only %s are accepted values for student programs".formatted(allowedProgramValues));
        }
    }
}
