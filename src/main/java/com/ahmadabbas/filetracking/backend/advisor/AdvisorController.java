package com.ahmadabbas.filetracking.backend.advisor;

import com.ahmadabbas.filetracking.backend.advisor.payload.AdvisorDto;
import com.ahmadabbas.filetracking.backend.advisor.payload.AdvisorMapper;
import com.ahmadabbas.filetracking.backend.advisor.payload.AdvisorRegistrationRequest;
import com.ahmadabbas.filetracking.backend.user.User;
import com.ahmadabbas.filetracking.backend.util.payload.PaginatedResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/advisors")
@Tag(name = "Advisor")
public class AdvisorController {
    private final AdvisorService advisorService;
    private final AdvisorMapper advisorMapper;

    @Operation(summary = "Get advisor")
    @GetMapping("/{advisorId}")
    public ResponseEntity<AdvisorDto> getAdvisor(@PathVariable String advisorId,
                                                 @AuthenticationPrincipal User loggedInUser) {
        Advisor advisor = advisorService.getAdvisorByAdvisorId(advisorId, loggedInUser);
        return ResponseEntity.ok(advisorMapper.toDto(advisor));
    }

    @Operation(summary = "Add advisor")
    @PostMapping("")
    public ResponseEntity<AdvisorDto> registerAdvisor(@RequestBody AdvisorRegistrationRequest advisorRegistrationRequest) {
        Advisor addedAdvisor = advisorService.addAdvisor(advisorRegistrationRequest);
        return new ResponseEntity<>(advisorMapper.toDto(addedAdvisor), HttpStatus.CREATED);
    }

    @Operation(
            summary = "Get all advisors",
            description = "Returns a pagination result of all advisors in the database sorted by default on id and ascending order."
    )
    @GetMapping("")
    public ResponseEntity<PaginatedResponse<AdvisorDto>> getAllStudents(
            @RequestParam(defaultValue = "1", required = false) int pageNo,
            @RequestParam(defaultValue = "10", required = false) int pageSize,
            @RequestParam(defaultValue = "id", required = false) String sortBy,
            @RequestParam(defaultValue = "asc", required = false) String order,
            @RequestParam(defaultValue = "", required = false) String searchQuery
    ) {
        return ResponseEntity.ok(advisorService.getAllAdvisors(pageNo, pageSize, sortBy, order, searchQuery));
    }
}
