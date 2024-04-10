package com.ahmadabbas.filetracking.backend.email;

public enum EmailTemplate {
    ACTIVATE_ACCOUNT;

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
