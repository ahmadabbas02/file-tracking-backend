package com.ahmadabbas.filetracking.backend.entity;

import lombok.NonNull;

public interface EntityDtoMapper<Dto> {
    Dto toDto();

}
