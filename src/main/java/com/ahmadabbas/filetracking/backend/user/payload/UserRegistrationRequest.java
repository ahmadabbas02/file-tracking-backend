package com.ahmadabbas.filetracking.backend.user.payload;

import com.ahmadabbas.filetracking.backend.user.Role;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;

public class UserRegistrationRequest {
    @JsonProperty
    @NotEmpty(message = "user's firstName should not be empty")
    private final String firstName;
    @JsonProperty
    @NotEmpty(message = "user's lastName should not be empty")
    private final String lastName;
    @JsonProperty
    @Email(message = "user's email should be valid")
    @NotEmpty
    private final String email;
    @JsonProperty
    @NotEmpty(message = "user's picture should not be empty")
    private final String picture;
    @JsonProperty
    @NotEmpty(message = "user phoneNumber should not be empty")
    private final String phoneNumber;
    @JsonProperty
    @NotNull
    private final Role role;

    public UserRegistrationRequest(String firstName,
                                   String lastName,
                                   String email,
                                   String picture,
                                   String phoneNumber,
                                   Role role) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.picture = picture;
        this.phoneNumber = phoneNumber;
        this.role = role;
    }

    public String firstName() {
        return firstName.trim();
    }

    public String lastName() {
        return lastName.trim();
    }

    public String email() {
        return email.toLowerCase().trim();
    }

    public String picture() {
        return picture.trim();
    }

    public String phoneNumber() {
        return phoneNumber.trim();
    }

    public Role role() {
        return role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserRegistrationRequest that)) return false;
        return Objects.equals(firstName(), that.firstName())
               && Objects.equals(lastName(), that.lastName())
               && Objects.equals(email(), that.email())
               && Objects.equals(picture(), that.picture())
               && Objects.equals(phoneNumber(), that.phoneNumber())
               && role() == that.role();
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstName(), lastName(), email(), picture(), phoneNumber(), role());
    }
}
