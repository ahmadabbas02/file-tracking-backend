package com.ahmadabbas.filetracking.backend.util.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CsvUploadResponse {
    private int successCount;
    private int failedCount;
    private String failedReason;
}
