package com.aicopilot.controller;

import com.aicopilot.dto.ResumeAnalysisResponse;
import com.aicopilot.model.Resume;
import com.aicopilot.service.ResumeAnalysisService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/resumes")
public class ResumeController {

    private final ResumeAnalysisService resumeAnalysisService;

    public ResumeController(ResumeAnalysisService resumeAnalysisService) {
        this.resumeAnalysisService = resumeAnalysisService;
    }

    @PostMapping("/upload")
    public ResponseEntity<ResumeAnalysisResponse> uploadResume(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal UserDetails userDetails
    ) throws IOException {
        ResumeAnalysisResponse response = resumeAnalysisService.analyzeAndSave(file, userDetails.getUsername());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/analyze-text")
    public ResponseEntity<ResumeAnalysisResponse> analyzeResumeText(
            @RequestBody Map<String, String> payload,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String text = payload.get("text");
        String filename = payload.getOrDefault("filename", "pasted-resume.txt");
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("Resume text cannot be empty.");
        }
        ResumeAnalysisResponse response = resumeAnalysisService.analyzeRawText(text, filename, userDetails.getUsername());
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<Resume>> getUserResumes(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(resumeAnalysisService.getUserResumes(userDetails.getUsername()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResumeAnalysisResponse> getResume(
            @PathVariable("id") Long id,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(resumeAnalysisService.getResumeAnalysis(id, userDetails.getUsername()));
    }
}
