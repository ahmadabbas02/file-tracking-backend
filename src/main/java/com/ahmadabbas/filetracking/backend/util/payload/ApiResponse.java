package com.ahmadabbas.filetracking.backend.util.payload;

import java.util.StringJoiner;

public record ApiResponse<T>(String message, T results) {

    @Override
    public String toString() {
        return new StringJoiner(", ", ApiResponse.class.getSimpleName() + "[", "]")
                .add("results=" + results)
                .toString();
    }
}
