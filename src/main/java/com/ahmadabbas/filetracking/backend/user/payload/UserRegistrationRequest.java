package com.ahmadabbas.filetracking.backend.user.payload;

import com.ahmadabbas.filetracking.backend.user.Role;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;

public class UserRegistrationRequest {
    @JsonProperty
    @NotEmpty(message = "user's name should not be empty")
    private final String name;
    @JsonProperty
    @NotEmpty(message = "user's surname should not be empty")
    private final String surname;
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

    public UserRegistrationRequest(String name, String surname, String email, String picture, String phoneNumber, Role role) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.picture = picture;
        this.phoneNumber = phoneNumber;
        this.role = role;
    }

    public String name() {
        return name.trim();
    }

    public String surname() {
        return surname.trim();
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
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (UserRegistrationRequest) obj;
        return Objects.equals(this.name, that.name) &&
               Objects.equals(this.surname, that.surname) &&
               Objects.equals(this.email, that.email) &&
               Objects.equals(this.picture, that.picture) &&
               Objects.equals(this.phoneNumber, that.phoneNumber) &&
               Objects.equals(this.role, that.role);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, surname, email, picture, phoneNumber, role);
    }

    @Override
    public String toString() {
        return "UserRegistrationRequest[" +
               "name=" + name + ", " +
               "surname=" + surname + ", " +
               "email=" + email + ", " +
               "picture=" + picture + ", " +
               "phoneNumber=" + phoneNumber + ", " +
               "role=" + role + ']';
    }

}
