package com.ahmadabbas.filetracking.backend.document.internship;

import org.apache.commons.text.WordUtils;

public class InternshipStatus {
    public enum PaymentStatus {
        PAID,
        NOT_PAID
    }

    public enum CompletionStatus {
        COMPLETE,
        PARTIALLY_COMPLETED,
        INCOMPLETE;

        @Override
        public String toString() {
            return WordUtils.capitalize(name().toLowerCase().replaceAll("_", " "));
        }
    }
}
