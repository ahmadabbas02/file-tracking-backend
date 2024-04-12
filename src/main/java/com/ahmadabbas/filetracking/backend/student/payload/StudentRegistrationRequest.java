package com.ahmadabbas.filetracking.backend.student.payload;

import com.ahmadabbas.filetracking.backend.exception.APIException;
import com.ahmadabbas.filetracking.backend.user.Role;
import com.ahmadabbas.filetracking.backend.user.payload.UserRegistrationRequest;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
public final class StudentRegistrationRequest extends UserRegistrationRequest implements Serializable {

    public static final String[] ALLOWED_PROGRAM_VALUES = {"CMSE", "CMPE", "BLGM"};

    @JsonProperty
    private final String id;
    @JsonProperty
    @NotEmpty(message = "student's program should not be empty")
    private final String program;
    @JsonProperty
    @Positive
    @Min(1)
    @NotNull
    private final Short year;
    @JsonProperty
    @NotEmpty(message = "student's advisor id should not be empty")
    private final String advisorId;

    public StudentRegistrationRequest(String firstName,
                                      String lastName,
                                      String email,
                                      String picture,
                                      String phoneNumber,
                                      String id,
                                      String program,
                                      Short year,
                                      String advisorId) {
        super(firstName, lastName, email, picture, phoneNumber, Role.STUDENT);
        this.id = id;
        List<String> allowedProgramValues = Arrays.asList(ALLOWED_PROGRAM_VALUES);
        if (!allowedProgramValues.contains(program)) {
            throw new APIException(HttpStatus.BAD_REQUEST,
                    "Only %s are accepted values for student programs".formatted(allowedProgramValues));
        }
        this.program = program;
        this.year = year;
        this.advisorId = advisorId;
    }

    public String program() {
        return program;
    }

    public Short year() {
        return year;
    }

    public String advisorId() {
        return advisorId;
    }

    public String id() {
        return id;
    }
}
