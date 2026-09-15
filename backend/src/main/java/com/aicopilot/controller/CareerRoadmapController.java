package com.aicopilot.controller;

import com.aicopilot.dto.RoadmapRequest;
import com.aicopilot.dto.RoadmapResponse;
import com.aicopilot.service.CareerRoadmapService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roadmaps")
public class CareerRoadmapController {

    private final CareerRoadmapService roadmapService;

    public CareerRoadmapController(CareerRoadmapService roadmapService) {
        this.roadmapService = roadmapService;
    }

    @PostMapping("/generate")
    public ResponseEntity<RoadmapResponse> generateRoadmap(
            @Valid @RequestBody RoadmapRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(roadmapService.generateRoadmap(request, userDetails.getUsername()));
    }

    @GetMapping
    public ResponseEntity<List<RoadmapResponse>> getUserRoadmaps(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(roadmapService.getUserRoadmaps(userDetails.getUsername()));
    }
}
