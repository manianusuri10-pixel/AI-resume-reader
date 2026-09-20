package com.aicopilot.config;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.aicopilot.model.User;
import com.aicopilot.repository.RagDocumentRepository;
import com.aicopilot.repository.UserRepository;
import com.aicopilot.service.RagService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class RagDataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(RagDataSeeder.class);

    private final RagDocumentRepository ragDocumentRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RagService ragService;
    private final ObjectMapper objectMapper;

    public RagDataSeeder(RagDocumentRepository ragDocumentRepository,
                         UserRepository userRepository,
                         PasswordEncoder passwordEncoder,
                         RagService ragService,
                         ObjectMapper objectMapper) {
        this.ragDocumentRepository = ragDocumentRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.ragService = ragService;
        this.objectMapper = objectMapper;
    }

    @Override
    public void run(String... args) {
        seedDemoUser();
        seedRagDocuments();
    }

    private void seedDemoUser() {
        User demoUser = userRepository.findByEmail("demo@aicopilot.com").orElseGet(() -> new User(
                "demo@aicopilot.com",
                passwordEncoder.encode("password123"),
                "Alex Mercer",
                "Senior Full Stack & AI Engineer",
                5
        ));

        if (!passwordEncoder.matches("password123", demoUser.getPassword())) {
            demoUser.setPassword(passwordEncoder.encode("password123"));
            userRepository.save(demoUser);
            log.info("Reset default demo user password: demo@aicopilot.com / password123");
        } else if (demoUser.getId() == null) {
            userRepository.save(demoUser);
            log.info("Seeded default demo user: demo@aicopilot.com / password123");
        }
    }

    private void seedRagDocuments() {
        if (ragDocumentRepository.count() == 0) {
            try {
                ClassPathResource resource = new ClassPathResource("data/rag-knowledge-base.json");
                if (resource.exists()) {
                    try (InputStream is = resource.getInputStream()) {
                        List<Map<String, String>> docs = objectMapper.readValue(is, new TypeReference<>() {});
                        for (Map<String, String> docMap : docs) {
                            String title = docMap.get("title");
                            String category = docMap.get("category");
                            String content = docMap.get("content");
                            String source = docMap.get("source");
                            ragService.saveAndIndex(title, category, content, source);
                        }
                        log.info("Successfully seeded and indexed {} RAG knowledge base documents", docs.size());
                    }
                }
            } catch (Exception e) {
                log.warn("Could not seed RAG documents from JSON: {}", e.getMessage());
            }
        }
    }
}
