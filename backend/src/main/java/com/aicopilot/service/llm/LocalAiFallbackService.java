package com.aicopilot.service.llm;

import com.aicopilot.dto.*;
import com.aicopilot.service.DocumentParserService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class LocalAiFallbackService implements LlmService {

    private final DocumentParserService parserService;

    public LocalAiFallbackService(DocumentParserService parserService) {
        this.parserService = parserService;
    }

    @Override
    public boolean isCloudLlmConfigured() {
        return false;
    }

    @Override
    public ResumeAnalysisResponse analyzeResume(
            String resumeText,
            String filename,
            Map<String, String> sections,
            List<String> extractedSkills,
            int actionVerbs,
            int metricsCount,
            String ragContext
    ) {
        ResumeAnalysisResponse response = new ResumeAnalysisResponse();
        response.setFileName(filename);
        response.setRawText(resumeText);
        response.setSections(sections);
        response.setExtractedSkills(extractedSkills);
        response.setActionVerbsCount(actionVerbs);
        response.setQuantifiableMetricsCount(metricsCount);
        response.setTotalWords(parserService.countWords(resumeText));
        response.setCreatedAt(LocalDateTime.now());

        // Formatting & Structure Score (0 - 100)
        int formatting = 50;
        if (sections.containsKey("SUMMARY")) formatting += 10;
        if (sections.containsKey("SKILLS")) formatting += 15;
        if (sections.containsKey("EXPERIENCE")) formatting += 15;
        if (sections.containsKey("EDUCATION")) formatting += 10;
        response.setFormattingScore(Math.min(100, formatting));

        // Impact & Metrics Score (0 - 100)
        int impact = Math.min(100, (actionVerbs * 3) + (metricsCount * 6) + 30);
        response.setImpactScore(impact);

        // ATS Composite Score
        int skillScore = Math.min(100, extractedSkills.size() * 7 + 25);
        int compositeAts = (int) Math.round((skillScore * 0.40) + (impact * 0.35) + (formatting * 0.25));
        response.setAtsScore(Math.min(98, Math.max(35, compositeAts)));

        // Strengths
        List<String> strengths = new ArrayList<>();
        if (extractedSkills.size() >= 5) {
            strengths.add("Strong technical keyword footprint detected (" + extractedSkills.size() + " industry-standard skills identified).");
        }
        if (metricsCount >= 2) {
            strengths.add("Effective use of quantifiable results (" + metricsCount + " data points and measurable metrics).");
        }
        if (actionVerbs >= 5) {
            strengths.add("Proactive language with " + actionVerbs + " strong leadership and engineering action verbs.");
        }
        if (sections.containsKey("EXPERIENCE") && sections.containsKey("SKILLS")) {
            strengths.add("Clean standard section hierarchy optimized for ATS parsers (Workday, Greenhouse, Lever).");
        }
        if (strengths.isEmpty()) {
            strengths.add("Structured foundation with readable chronological formatting.");
        }
        response.setStrengths(strengths);

        // Critical Improvements
        List<String> improvements = new ArrayList<>();
        if (metricsCount < 4) {
            improvements.add("Increase quantifiable business impact: Quantify achievements using the Google X-Y-Z formula: 'Accomplished [X], as measured by [Y], by doing [Z]'.");
        }
        if (actionVerbs < 6) {
            improvements.add("Replace passive phrasing ('responsible for', 'worked on') with authoritative verbs ('spearheaded', 'architected', 'orchestrated').");
        }
        if (!sections.containsKey("SUMMARY")) {
            improvements.add("Add a 3-4 sentence Professional Summary at the top highlighting total years of experience, core specialization, and key achievements.");
        }
        if (extractedSkills.size() < 8) {
            improvements.add("Broaden core technical competencies section to explicitly match target job requisitions and keyword filters.");
        }
        improvements.add("Ensure bullet points emphasize architectural trade-offs, system scalability, and business impact over routine maintenance tasks.");
        response.setCriticalImprovements(improvements);

        // Bullet Rewrites
        List<BulletRewriteDto> rewrites = new ArrayList<>();
        rewrites.add(new BulletRewriteDto(
                "Responsible for writing backend APIs and fixing bugs in the database.",
                "Architected high-throughput RESTful microservices in Spring Boot, optimizing database queries to reduce p99 response latency by 38%.",
                "Replaced passive duty description with an impactful action verb ('Architected') and measurable outcome."
        ));
        rewrites.add(new BulletRewriteDto(
                "Helped team migrate legacy application to modern cloud architecture.",
                "Spearheaded containerization and CI/CD migration of 12 monolithic modules into Dockerized microservices on AWS, accelerating release velocity by 4x.",
                "Specified scale (12 modules), tech stack (Docker, AWS), and quantifiable velocity improvement."
        ));
        rewrites.add(new BulletRewriteDto(
                "Worked on frontend user interface and improved page loading time.",
                "Refactored React component tree using memoization and code-splitting, reducing Core Web Vitals Largest Contentful Paint (LCP) from 3.4s to 1.1s.",
                "Replaced generic 'worked on' with precise technical mechanism and benchmarked timing metric."
        ));
        response.setBulletRewrites(rewrites);

        return response;
    }

    @Override
    public JobMatchResponse matchJob(
            String resumeText,
            List<String> resumeSkills,
            String jobDescription,
            String targetRole,
            String targetCompany,
            String ragContext
    ) {
        JobMatchResponse response = new JobMatchResponse();
        response.setTargetRole(targetRole != null && !targetRole.isBlank() ? targetRole : "Software Engineer");
        response.setTargetCompany(targetCompany != null && !targetCompany.isBlank() ? targetCompany : "Target Organization");
        response.setCreatedAt(LocalDateTime.now());

        List<String> jdSkills = parserService.extractSkills(jobDescription);
        if (jdSkills.isEmpty()) {
            jdSkills = List.of("Java", "Spring Boot", "React", "Docker", "SQL", "REST APIs", "Git");
        }

        Set<String> resumeSkillSet = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        resumeSkillSet.addAll(resumeSkills);

        List<String> matched = new ArrayList<>();
        List<String> missing = new ArrayList<>();

        for (String skill : jdSkills) {
            if (resumeSkillSet.contains(skill) || resumeText.toLowerCase().contains(skill.toLowerCase())) {
                matched.add(skill);
            } else {
                missing.add(skill);
            }
        }

        response.setMatchedSkills(matched);
        response.setMissingSkills(missing);

        int totalSkills = Math.max(1, jdSkills.size());
        int matchPct = (int) Math.round(((double) matched.size() / totalSkills) * 100.0);
        response.setSkillMatchPercentage(matchPct);

        // Overall match score (adjusted for context)
        int overallScore = Math.min(96, Math.max(30, (int) (matchPct * 0.75 + 20)));
        response.setMatchScore(overallScore);

        // Recommendations
        List<String> recs = new ArrayList<>();
        if (!missing.isEmpty()) {
            recs.add("Prioritize highlighting or upskilling in high-demand gaps: " + String.join(", ", missing.subList(0, Math.min(3, missing.size()))) + ".");
        }
        recs.add("Tailor your professional summary to mirror the primary language of the job description for " + response.getTargetRole() + ".");
        recs.add("Embed the matched technologies directly into your recent employment experience bullet points rather than just listing them in a skills section.");
        response.setRecommendations(recs);

        // Tailored Elevator Pitch
        String pitch = String.format(
                "Dear Hiring Team at %s,\n\n" +
                "I am excited to submit my candidacy for the %s position. With a strong engineering background in %s, " +
                "I have proven experience delivering robust, scalable systems and driving measurable business outcomes. " +
                "My hands-on experience in %s directly aligns with your requirements, and I am eager to leverage these capabilities to accelerate your team's technical milestones.\n\n" +
                "Sincerely,\nCandidate",
                response.getTargetCompany(),
                response.getTargetRole(),
                !matched.isEmpty() ? String.join(", ", matched.subList(0, Math.min(3, matched.size()))) : "full-stack distributed architecture",
                !matched.isEmpty() ? matched.get(0) : "modern cloud development"
        );
        response.setTailoredPitch(pitch);

        return response;
    }

    @Override
    public RoadmapResponse generateRoadmap(
            String currentRole,
            String targetRole,
            int timelineMonths,
            List<String> currentSkills,
            String ragContext
    ) {
        RoadmapResponse response = new RoadmapResponse();
        response.setCurrentRole(currentRole != null && !currentRole.isBlank() ? currentRole : "Software Developer");
        response.setTargetRole(targetRole != null && !targetRole.isBlank() ? targetRole : "Senior Full-Stack & AI Engineer");
        response.setTimelineMonths(timelineMonths > 0 ? timelineMonths : 6);
        response.setEstimatedWeeklyHours(12);
        response.setCreatedAt(LocalDateTime.now());

        String targetLower = response.getTargetRole().toLowerCase();

        List<MilestoneDto> milestones = new ArrayList<>();
        List<String> skillsToAcquire = new ArrayList<>();

        if (targetLower.contains("java") || targetLower.contains("backend") || targetLower.contains("cloud")) {
            skillsToAcquire.addAll(List.of("Spring Boot 3", "Project Loom (Virtual Threads)", "Microservices Architecture", "Kafka Streaming", "Docker & Kubernetes", "Distributed Caching (Redis)", "System Design"));

            milestones.add(new MilestoneDto(
                    1,
                    "Core Backend Foundations & Modern Java/Spring",
                    4,
                    "Master Modern Java 21 features and Spring Boot 3 autoconfiguration and security.",
                    List.of("Java 21 Records & Pattern Matching", "Spring Boot 3 & Spring Security 6 JWT", "Hibernate/JPA N+1 query optimization", "HikariCP connection tuning"),
                    List.of("Refactor existing backend service to use Virtual Threads", "Implement stateless JWT authentication with token rotation", "Set up unit and integration test suite with Testcontainers"),
                    List.of("Spring Boot 3 In Action", "Baeldung Spring Security Deep Dive", "Refactoring to Patterns")
            ));

            milestones.add(new MilestoneDto(
                    2,
                    "Distributed Systems & Event-Driven Architecture",
                    6,
                    "Design decoupled, resilient services using asynchronous messaging and distributed caching.",
                    List.of("Apache Kafka event streaming & consumer groups", "Redis cache-aside and rate-limiting patterns", "Resilience4j Circuit Breaker & Retry", "Database sharding and read replicas"),
                    List.of("Build an event-driven order processing pipeline with Kafka", "Implement Redis distributed lock for concurrent transactions", "Configure circuit breakers with fallback telemetry"),
                    List.of("Designing Data-Intensive Applications (Martin Kleppmann)", "Kafka: The Definitive Guide")
            ));

            milestones.add(new MilestoneDto(
                    3,
                    "Cloud, Containers & DevOps Orchestration",
                    5,
                    "Containerize services and orchestrate automated CI/CD pipelines to AWS/Kubernetes.",
                    List.of("Docker multi-stage lightweight builds", "Kubernetes Pods, Services, Ingress, and Helm", "GitHub Actions CI/CD workflows", "Terraform Infrastructure as Code"),
                    List.of("Deploy microservices on a local Kubernetes cluster (Minikube/k3s)", "Automate build, test, and container scanning in CI", "Provision AWS resources via Terraform"),
                    List.of("Kubernetes Up & Running", "Docker Deep Dive (Nigel Poulton)")
            ));

            milestones.add(new MilestoneDto(
                    4,
                    "High-Level System Design & Staff Interview Readiness",
                    5,
                    "Master large-scale system design tradeoffs, mock interviews, and behavioral leadership.",
                    List.of("CAP Theorem & PACELC", "Consistent Hashing & Partitioning", "API Gateway & Reverse Proxy (Nginx/Kong)", "STAR Method leadership behavioral stories"),
                    List.of("Design YouTube/Netflix video streaming architecture", "Practice 10 mock system design whiteboard interviews", "Document 5 key engineering challenges overcome"),
                    List.of("System Design Interview (Alex Xu Vol 1 & 2)", "Grokking Modern System Design")
            ));
        } else {
            // Fullstack / General AI & Web Roadmap
            skillsToAcquire.addAll(List.of("React 18 / TypeScript", "RESTful API Integration", "RAG & Vector Embeddings", "Full-Stack Security (JWT)", "Tailwind CSS", "Docker Containerization", "Mock Interview Mastery"));

            milestones.add(new MilestoneDto(
                    1,
                    "Full-Stack Foundations & Modern UI Engineering",
                    4,
                    "Master React 18, TypeScript strict typing, and responsive modern frontend styling.",
                    List.of("React 18 Concurrent features & hooks", "Strict TypeScript generics and interfaces", "Tailwind CSS utility architecture", "State management with Zustand/Redux"),
                    List.of("Build a responsive component library with Tailwind", "Implement client-side authentication and route guards", "Integrate RESTful APIs with Axios interceptors"),
                    List.of("Fullstack React with TypeScript", "Official React Documentation")
            ));

            milestones.add(new MilestoneDto(
                    2,
                    "AI Integration, RAG Architectures & Document Processing",
                    6,
                    "Integrate LLMs, build local RAG pipelines, and parse complex document formats.",
                    List.of("Retrieval-Augmented Generation (RAG) principles", "Vector similarity search (Cosine metric)", "Document extraction (PDFBox, OCR)", "LLM prompt engineering and schema validation"),
                    List.of("Implement vector store chunking and cosine similarity index", "Build an automated PDF parsing pipeline", "Create an interactive AI chat interface with context augmentation"),
                    List.of("DeepLearning.AI LangChain & RAG courses", "Pinecone Vector Search Handbook")
            ));

            milestones.add(new MilestoneDto(
                    3,
                    "Production Readiness, Security & Dockerization",
                    5,
                    "Package frontend and backend into unified containers with production security.",
                    List.of("Docker multi-stage builds & Nginx reverse proxy", "Stateless JWT authentication & CORS handling", "Database indexing and persistent storage volumes", "Automated deployment configurations"),
                    List.of("Containerize full-stack application using docker-compose", "Implement end-to-end integration tests", "Conduct security audit for OWASP Top 10 vulnerabilities"),
                    List.of("Docker & Kubernetes for Web Developers", "OWASP Secure Coding Guidelines")
            ));

            milestones.add(new MilestoneDto(
                    4,
                    "Portfolio Launch & Technical Interview Preparation",
                    5,
                    "Polish portfolio project, deploy to cloud staging, and practice live mock interview sessions.",
                    List.of("Live cloud deployment (AWS/GCP/Vercel)", "Technical interview communication & whiteboard problem solving", "STAR behavioral story formulation", "Salary negotiation strategies"),
                    List.of("Publish open-source code repository with rich README and demo links", "Complete 5 interactive mock interview sessions with AI Copilot", "Refine LinkedIn profile and resume targeting high-impact roles"),
                    List.of("Cracking the Coding Interview (Gayle Laakmann McDowell)", "Tech Interview Handbook")
            ));
        }

        response.setSkillsToAcquire(skillsToAcquire);
        response.setMilestones(milestones);
        return response;
    }

    @Override
    public List<InterviewQuestionDto> generateInterviewQuestions(
            String targetRole,
            String difficulty,
            int count,
            String ragContext
    ) {
        List<InterviewQuestionDto> list = new ArrayList<>();
        int qCount = Math.max(1, Math.min(5, count));

        String role = targetRole.toLowerCase();

        if (role.contains("java") || role.contains("backend") || role.contains("spring")) {
            InterviewQuestionDto q1 = new InterviewQuestionDto();
            q1.setQuestionNumber(1);
            q1.setCategory("FRAMEWORKS_CONCURRENCY");
            q1.setQuestionText("Explain how Spring Boot manages bean lifecycles and how @Transactional works under the hood. Specifically, what happens when a transactional method is called from within the same class?");
            q1.setSampleIdealAnswer("Spring uses CGLIB or JDK dynamic proxies to intercept method calls annotated with @Transactional. When called from an external class, the proxy intercepts the invocation, starts a database transaction via the TransactionManager, executes the method, and commits or rolls back on exception. However, if a transactional method is called from within the same class (self-invocation), the call bypasses the proxy and invokes the target object directly, meaning the @Transactional annotation is silently ignored.");
            list.add(q1);

            if (qCount >= 2) {
                InterviewQuestionDto q2 = new InterviewQuestionDto();
                q2.setQuestionNumber(2);
                q2.setCategory("DATABASE_OPTIMIZATION");
                q2.setQuestionText("What causes the Hibernate N+1 query problem in Spring Data JPA, and what are the primary architectural techniques to eliminate it in high-throughput applications?");
                q2.setSampleIdealAnswer("The N+1 problem occurs when fetching an entity with a one-to-many or many-to-one relationship: Hibernate executes 1 query to fetch the parent entities, and then N additional queries to fetch the associated child entities for each parent. To eliminate it: 1) Use JOIN FETCH in JPQL queries. 2) Apply @EntityGraph to define fetch graphs declaratively. 3) Configure batch fetching using @BatchSize(size = 25). 4) Use DTO projection queries to select only the necessary columns directly.");
                list.add(q2);
            }

            if (qCount >= 3) {
                InterviewQuestionDto q3 = new InterviewQuestionDto();
                q3.setQuestionNumber(3);
                q3.setCategory("SYSTEM_DESIGN");
                q3.setQuestionText("How would you design a distributed caching layer using Redis to prevent Cache Stampede (Thundering Herd) and Cache Penetration in a microservices system?");
                q3.setSampleIdealAnswer("To prevent Cache Stampede: 1) Implement distributed locking (e.g. Redisson) so only one instance queries the primary DB on cache miss while other threads wait. 2) Use probabilistic early expiration (XFetch algorithm) to asynchronously refresh keys before they expire. To prevent Cache Penetration: 1) Cache null objects with a short TTL for non-existent keys. 2) Deploy a Bloom Filter in front of Redis to immediately reject requests for identifiers that definitely do not exist in the database.");
                list.add(q3);
            }
        } else {
            // General / Full-Stack / Frontend Questions
            InterviewQuestionDto q1 = new InterviewQuestionDto();
            q1.setQuestionNumber(1);
            q1.setCategory("FRONTEND_REACT");
            q1.setQuestionText("How does the React 18 reconciliation algorithm determine whether to re-render a component, and how do useMemo and useCallback optimize performance without causing stale closure issues?");
            q1.setSampleIdealAnswer("React uses a fiber tree reconciliation algorithm that runs whenever component state or props change. It performs shallow equality comparisons (Object.is). useCallback memoizes a callback function reference, while useMemo memoizes the computed value of a function between renders. To avoid stale closures, all mutable variables and state values accessed inside the memoized function must be explicitly declared in the dependency array.");
            list.add(q1);

            if (qCount >= 2) {
                InterviewQuestionDto q2 = new InterviewQuestionDto();
                q2.setQuestionNumber(2);
                q2.setCategory("SECURITY_AUTHENTICATION");
                q2.setQuestionText("Describe how stateless JWT authentication operates across a React frontend and Spring Boot backend. How do you mitigate token theft, XSS, and CSRF attacks?");
                q2.setSampleIdealAnswer("Upon successful authentication, the backend signs a cryptographic JWT (HMAC-SHA256) containing subject and claims. The frontend stores it (ideally in memory or HttpOnly, SameSite=Strict cookies) and attaches it in the Authorization: Bearer header for subsequent requests. The backend validates signature and expiration in a OncePerRequestFilter without server-side session state. XSS is mitigated by escaping user input and strict Content Security Policy; CSRF is mitigated by stateless header-based tokens and SameSite cookies; short token expiration with refresh tokens limits window of exposure.");
                list.add(q2);
            }

            if (qCount >= 3) {
                InterviewQuestionDto q3 = new InterviewQuestionDto();
                q3.setQuestionNumber(3);
                q3.setCategory("RAG_ARCHITECTURE");
                q3.setQuestionText("What is Retrieval-Augmented Generation (RAG) and how does vector similarity search (such as Cosine Similarity) ground LLM responses to reduce hallucinations?");
                q3.setSampleIdealAnswer("RAG combines vector retrieval with generative language models. Documents are chunked and converted to dense vector embeddings. When a user submits a prompt, the query is converted into an embedding and compared against the document vectors using Cosine Similarity. The top-k most semantically relevant chunks are retrieved and injected into the LLM system prompt as verified context. This grounds the model in authoritative facts, mitigates hallucination, and enables the model to access private or real-time domain knowledge without fine-tuning.");
                list.add(q3);
            }
        }

        return list;
    }

    @Override
    public InterviewEvaluationResponse evaluateInterviewAnswer(
            String questionText,
            String userResponse,
            String difficulty,
            String sampleIdealAnswer,
            String ragContext
    ) {
        InterviewEvaluationResponse response = new InterviewEvaluationResponse();
        response.setModelIdealAnswer(sampleIdealAnswer);

        if (userResponse == null || userResponse.trim().length() < 15) {
            response.setScore(2.5);
            response.setCritique("The response is too brief and lacks essential technical depth.");
            response.setStrengths("Attempted to respond to the prompt.");
            response.setMissingConcepts("Detailed architectural explanation, edge case considerations, and practical production examples.");
            response.setSuggestedImprovement("Elaborate with concrete architectural mechanisms, step-by-step lifecycle flow, and trade-off analysis.");
            return response;
        }

        int wordCount = userResponse.trim().split("\\s+").length;
        String lower = userResponse.toLowerCase();

        // Technical depth heuristic scoring
        double baseScore = 5.0;
        if (wordCount >= 50) baseScore += 1.5;
        if (wordCount >= 100) baseScore += 1.0;

        List<String> keyTerms = List.of(
                "proxy", "transaction", "cache", "latency", "concurrency", "thread", "virtual", "memory",
                "reconciliation", "memo", "jwt", "stateless", "rag", "vector", "embedding", "cosine", "query", "index"
        );

        int matchedTerms = 0;
        for (String term : keyTerms) {
            if (lower.contains(term)) matchedTerms++;
        }

        baseScore += Math.min(2.5, matchedTerms * 0.5);
        double finalScore = Math.min(9.8, Math.max(3.0, Math.round(baseScore * 10.0) / 10.0));
        response.setScore(finalScore);

        if (finalScore >= 8.0) {
            response.setCritique("Excellent response. Demonstrates strong command of the underlying engineering principles, architectural mechanics, and edge cases.");
            response.setStrengths("Clear conceptual structure, correct technical terminology, and awareness of real-world system behavior.");
            response.setMissingConcepts("Minor deep-dive into observability (metrics/telemetry) or automated load testing under extreme conditions.");
            response.setSuggestedImprovement("To achieve a 10/10 Staff-level response, articulate specific metrics you would monitor in Grafana/Prometheus (e.g., p99 latency spikes, cache hit ratios).");
        } else if (finalScore >= 6.0) {
            response.setCritique("Good foundational answer. You correctly identified the primary concept, but could expand more on internal mechanics and corner cases.");
            response.setStrengths("Accurate understanding of high-level behavior and core definitions.");
            response.setMissingConcepts("Detailed discussion of proxy mechanics, failure modes, or concrete configuration parameters.");
            response.setSuggestedImprovement("Structure your explanation using: 1) What the problem is, 2) Why it occurs under the hood, 3) Two concrete solutions with trade-offs.");
        } else {
            response.setCritique("Fair attempt, but the answer remains superficial and does not fully address the underlying mechanics.");
            response.setStrengths("Recognized the general area of technology.");
            response.setMissingConcepts("Core architectural principles, root cause explanation, and production mitigation strategies.");
            response.setSuggestedImprovement("Review the sample ideal answer below and practice explaining the exact sequence of events step-by-step.");
        }

        return response;
    }

    @Override
    public String answerCareerQuestionWithRag(String query, String ragContext) {
        StringBuilder answer = new StringBuilder();
        answer.append("Based on curated industry engineering benchmarks and career progression rubrics:\n\n");
        if (ragContext != null && !ragContext.isBlank()) {
            answer.append(ragContext).append("\n\n");
        }
        answer.append("### Key Recommendations:\n");
        answer.append("1. **Focus on Measurable Architectural Impact**: Structure all career milestones and resume accomplishments using quantifiable outcomes (e.g. latency reductions, cloud cost optimizations, system throughput).\n");
        answer.append("2. **Master Deep Fundamentals Over Surface Syntax**: Interview evaluations heavily weight concurrency models, caching trade-offs, and distributed data consistency over basic framework usage.\n");
        answer.append("3. **Align Target Requisitions**: Continuously run gap analyses between your target job requisitions and your active project portfolio to prioritize high-leverage skills.\n");
        return answer.toString();
    }
}
