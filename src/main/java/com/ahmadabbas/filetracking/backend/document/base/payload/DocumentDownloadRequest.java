package com.ahmadabbas.filetracking.backend.document.base.payload;

import java.util.List;
import java.util.UUID;

public record DocumentDownloadRequest(List<UUID> uuids) {
}
