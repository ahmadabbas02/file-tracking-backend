package com.ahmadabbas.filetracking.backend.document.base.payload;

import com.ahmadabbas.filetracking.backend.document.base.DocumentStatus;
import jakarta.validation.constraints.NotNull;

public record DocumentApproveRequest(
        @NotNull DocumentStatus.ApprovalStatus approvalStatus
) {
}
