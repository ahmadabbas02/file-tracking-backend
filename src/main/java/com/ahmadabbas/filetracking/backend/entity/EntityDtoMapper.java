package com.ahmadabbas.filetracking.backend.entity;

import lombok.NonNull;

public interface EntityDtoMapper<D> {
    D toDto();
}
