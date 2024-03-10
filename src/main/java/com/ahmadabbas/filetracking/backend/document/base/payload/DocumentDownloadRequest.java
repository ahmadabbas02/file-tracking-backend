package com.ahmadabbas.filetracking.backend.document.base.payload;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;
import java.util.UUID;

public record DocumentDownloadRequest(
        @NotEmpty(message = "uuids should contain atleast one uuid to download") List<UUID> uuids
) {
}
