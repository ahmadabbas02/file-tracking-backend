package com.ahmadabbas.filetracking.backend.advisor;

import com.ahmadabbas.filetracking.backend.user.UserDtoMapper;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class AdvisorDtoMapper implements Function<Advisor, AdvisorDto> {


    private final UserDtoMapper userDtoMapper;

    public AdvisorDtoMapper(UserDtoMapper userDtoMapper) {
        this.userDtoMapper = userDtoMapper;
    }

    @Override
    public AdvisorDto apply(Advisor advisor) {
        return new AdvisorDto(
                advisor.getId(),
                userDtoMapper.apply(advisor.getUser()),
                advisor.getCreatedAt()
        );
    }
}
