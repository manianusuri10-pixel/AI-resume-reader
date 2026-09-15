package com.aicopilot.controller;

import com.aicopilot.dto.JobMatchRequest;
import com.aicopilot.dto.JobMatchResponse;
import com.aicopilot.model.JobMatch;
import com.aicopilot.service.JobMatchService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/job-match")
public class JobMatchController {

    private final JobMatchService jobMatchService;

    public JobMatchController(JobMatchService jobMatchService) {
        this.jobMatchService = jobMatchService;
    }

    @PostMapping("/analyze")
    public ResponseEntity<JobMatchResponse> matchJob(
            @Valid @RequestBody JobMatchRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(jobMatchService.matchJobDescription(request, userDetails.getUsername()));
    }

    @GetMapping("/history")
    public ResponseEntity<List<JobMatch>> getHistory(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(jobMatchService.getUserJobMatches(userDetails.getUsername()));
    }
}
