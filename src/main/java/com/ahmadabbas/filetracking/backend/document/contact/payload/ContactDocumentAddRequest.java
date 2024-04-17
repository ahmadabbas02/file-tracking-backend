package com.ahmadabbas.filetracking.backend.document.contact.payload;

import com.ahmadabbas.filetracking.backend.document.base.payload.BaseDocumentAddRequest;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;

public final class ContactDocumentAddRequest extends BaseDocumentAddRequest {
    @JsonProperty
    @Email(message = "Contact form email should be valid")
    private final String email;
    @JsonProperty
    @NotEmpty(message = "Contact form phone number should not be empty")
    private final String phoneNumber;
    @JsonProperty
    @NotEmpty(message = "Contact form home number should not be empty")
    private final String homeNumber;
    @JsonProperty
    @NotEmpty(message = "Contact form emergency name should not be empty")
    private final String emergencyName;
    @JsonProperty
    @NotEmpty(message = "Contact form phone number description should not be empty")
    private final String emergencyPhoneNumber;

    public ContactDocumentAddRequest(String title,
                                     String description,
                                     Long categoryId,
                                     String email,
                                     String phoneNumber,
                                     String homeNumber,
                                     String emergencyName,
                                     String emergencyPhoneNumber) {
        super(title, description, categoryId);
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.homeNumber = homeNumber;
        this.emergencyName = emergencyName;
        this.emergencyPhoneNumber = emergencyPhoneNumber;
    }

    public String email() {
        return email.trim().toLowerCase();
    }

    public String phoneNumber() {
        return phoneNumber;
    }

    public String homeNumber() {
        return homeNumber;
    }

    public String emergencyName() {
        return emergencyName;
    }

    public String emergencyPhoneNumber() {
        return emergencyPhoneNumber;
    }
}
