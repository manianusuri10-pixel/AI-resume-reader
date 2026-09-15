package com.aicopilot.service;

import com.aicopilot.dto.MilestoneDto;
import com.aicopilot.dto.RoadmapRequest;
import com.aicopilot.dto.RoadmapResponse;
import com.aicopilot.model.CareerRoadmap;
import com.aicopilot.model.User;
import com.aicopilot.repository.CareerRoadmapRepository;
import com.aicopilot.repository.UserRepository;
import com.aicopilot.service.llm.LlmService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CareerRoadmapService {

    private final CareerRoadmapRepository roadmapRepository;
    private final UserRepository userRepository;
    private final RagService ragService;
    private final LlmService llmService;
    private final ObjectMapper objectMapper;

    public CareerRoadmapService(CareerRoadmapRepository roadmapRepository,
                                UserRepository userRepository,
                                RagService ragService,
                                LlmService llmService,
                                ObjectMapper objectMapper) {
        this.roadmapRepository = roadmapRepository;
        this.userRepository = userRepository;
        this.ragService = ragService;
        this.llmService = llmService;
        this.objectMapper = objectMapper;
    }

    public RoadmapResponse generateRoadmap(RoadmapRequest request, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userEmail));

        String currentRole = request.getCurrentRole() != null ? request.getCurrentRole() : "Software Engineer";
        String targetRole = request.getTargetRole() != null ? request.getTargetRole() : user.getTargetRole();
        int timelineMonths = java.util.Optional.ofNullable(request.getTimelineMonths()).orElse(6);

        String ragContext = ragService.buildRagContext("Competency matrix and learning progression for " + targetRole, 3);

        RoadmapResponse response = llmService.generateRoadmap(
                currentRole,
                targetRole,
                timelineMonths,
                request.getCurrentSkills(),
                ragContext
        );

        CareerRoadmap roadmap = new CareerRoadmap();
        roadmap.setUser(user);
        roadmap.setCurrentRole(response.getCurrentRole());
        roadmap.setTargetRole(response.getTargetRole());
        roadmap.setTimelineMonths(response.getTimelineMonths());

        try {
            roadmap.setMilestonesJson(objectMapper.writeValueAsString(response.getMilestones()));
            roadmap.setRecommendedSkillsJson(objectMapper.writeValueAsString(response.getSkillsToAcquire()));
        } catch (JsonProcessingException e) {
            roadmap.setMilestonesJson("[]");
            roadmap.setRecommendedSkillsJson("[]");
        }

        CareerRoadmap saved = roadmapRepository.save(roadmap);
        response.setRoadmapId(saved.getId());

        return response;
    }

    public List<RoadmapResponse> getUserRoadmaps(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userEmail));

        List<CareerRoadmap> entities = roadmapRepository.findByUserOrderByCreatedAtDesc(user);
        List<RoadmapResponse> responses = new ArrayList<>();

        for (CareerRoadmap e : entities) {
            RoadmapResponse r = new RoadmapResponse();
            r.setRoadmapId(e.getId());
            r.setCurrentRole(e.getCurrentRole());
            r.setTargetRole(e.getTargetRole());
            r.setTimelineMonths(e.getTimelineMonths());
            r.setEstimatedWeeklyHours(12);
            r.setCreatedAt(e.getCreatedAt());
            try {
                r.setMilestones(objectMapper.readValue(e.getMilestonesJson(), new TypeReference<List<MilestoneDto>>() {}));
                r.setSkillsToAcquire(objectMapper.readValue(e.getRecommendedSkillsJson(), new TypeReference<List<String>>() {}));
            } catch (Exception ex) {
                r.setMilestones(new ArrayList<>());
                r.setSkillsToAcquire(new ArrayList<>());
            }
            responses.add(r);
        }

        return responses;
    }
}
