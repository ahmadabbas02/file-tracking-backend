package com.ahmadabbas.filetracking.backend.advisor;

import com.ahmadabbas.filetracking.backend.advisor.view.AdvisorUserView;
import com.ahmadabbas.filetracking.backend.user.User;
import com.ahmadabbas.filetracking.backend.util.payload.PaginatedResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/advisors")
@Tag(name = "Advisor")
public class AdvisorController {
    private final AdvisorService advisorService;

    @Operation(summary = "Get advisor")
    @GetMapping("/{advisorId}")
    public ResponseEntity<AdvisorUserView> getAdvisor(@PathVariable String advisorId,
                                                      @AuthenticationPrincipal User loggedInUser) {
        AdvisorUserView advisor = advisorService.getAdvisorViewByAdvisorId(advisorId, loggedInUser);
        return ResponseEntity.ok(advisor);
    }

    @Operation(
            summary = "Get all advisors",
            description = "Returns a pagination result of all advisors in the database sorted by default on id and ascending order."
    )
    @GetMapping("")
    public ResponseEntity<PaginatedResponse<AdvisorUserView>> getAllStudents(
            @RequestParam(defaultValue = "1", required = false) int pageNo,
            @RequestParam(defaultValue = "10", required = false) int pageSize,
            @RequestParam(defaultValue = "id", required = false) String sortBy,
            @RequestParam(defaultValue = "asc", required = false) String order,
            @RequestParam(defaultValue = "", required = false) String searchQuery
    ) {
        return ResponseEntity.ok(advisorService.getAllAdvisors(pageNo, pageSize, sortBy, order, searchQuery));
    }
}
