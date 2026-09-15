package com.aicopilot.controller;

import com.aicopilot.dto.InterviewSessionResponse;
import com.aicopilot.dto.InterviewStartRequest;
import com.aicopilot.dto.InterviewSubmitAnswerRequest;
import com.aicopilot.service.InterviewService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/interviews")
public class InterviewController {

    private final InterviewService interviewService;

    public InterviewController(InterviewService interviewService) {
        this.interviewService = interviewService;
    }

    @PostMapping("/start")
    public ResponseEntity<InterviewSessionResponse> startSession(
            @Valid @RequestBody InterviewStartRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(interviewService.startSession(request, userDetails.getUsername()));
    }

    @PostMapping("/submit")
    public ResponseEntity<InterviewSessionResponse> submitAnswer(
            @Valid @RequestBody InterviewSubmitAnswerRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(interviewService.submitAnswer(request, userDetails.getUsername()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<InterviewSessionResponse> getSession(
            @PathVariable("id") Long id,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(interviewService.getSession(id, userDetails.getUsername()));
    }

    @GetMapping
    public ResponseEntity<List<InterviewSessionResponse>> getUserSessions(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(interviewService.getUserSessions(userDetails.getUsername()));
    }
}
