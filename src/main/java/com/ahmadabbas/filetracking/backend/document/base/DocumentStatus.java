package com.ahmadabbas.filetracking.backend.document.base;

public class DocumentStatus {
    public enum InternshipPaymentStatus {
        PAID,
        NOT_PAID
    }

    public enum InternshipCompletionStatus {
        COMPLETE,
        INCOMPLETE,
        PARTIALLY_COMPLETED
    }

    public enum ApprovalStatus {
        APPROVED,
        PENDING,
        REJECTED
    }
}
