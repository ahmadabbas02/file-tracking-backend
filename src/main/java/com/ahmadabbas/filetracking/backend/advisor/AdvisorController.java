package com.ahmadabbas.filetracking.backend.advisor;

import com.ahmadabbas.filetracking.backend.advisor.payload.AdvisorRegistrationRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/advisor")
public class AdvisorController {
    private final AdvisorService advisorService;
    private final AdvisorDtoMapper advisorDtoMapper;

    public AdvisorController(AdvisorService advisorService, AdvisorDtoMapper advisorDtoMapper) {
        this.advisorService = advisorService;
        this.advisorDtoMapper = advisorDtoMapper;
    }

    @PostMapping
    public ResponseEntity<AdvisorDto> registerAdvisor(@RequestBody AdvisorRegistrationRequest advisorRegistrationRequest) {
        Advisor addedAdvisor = advisorService.addAdvisor(advisorRegistrationRequest);
        return new ResponseEntity<>(advisorDtoMapper.apply(addedAdvisor), HttpStatus.CREATED);
    }
}
