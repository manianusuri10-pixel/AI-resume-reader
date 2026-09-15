package com.aicopilot.service;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class DocumentParserService {

    private static final List<String> TECH_DICTIONARY = List.of(
            "java", "spring boot", "spring cloud", "spring security", "hibernate", "jpa",
            "react", "typescript", "javascript", "next.js", "redux", "zustand", "tailwind css", "html5", "css3",
            "angular", "vue", "node.js", "express", "python", "fastapi", "django", "flask",
            "c++", "c#", ".net", "go", "golang", "rust",
            "mysql", "postgresql", "oracle", "sql server", "mongodb", "redis", "cassandra", "dynamodb",
            "docker", "kubernetes", "helm", "terraform", "ansible", "jenkins", "github actions", "gitlab ci",
            "aws", "amazon web services", "gcp", "google cloud", "azure", "ec2", "s3", "lambda", "ecs", "eks",
            "kafka", "rabbitmq", "activemq", "sqs", "sns", "graphql", "rest", "grpc", "microservices",
            "rag", "llm", "langchain", "llama", "openai", "embeddings", "vector database", "pytorch", "tensorflow",
            "git", "linux", "bash", "agile", "scrum", "jira", "tdd", "junit", "mockito", "vitest", "jest"
    );

    private static final List<String> ACTION_VERBS = List.of(
            "accelerated", "achieved", "analyzed", "architected", "automated", "built", "centralized",
            "collaborated", "conceived", "consolidated", "created", "decreased", "delivered", "deployed",
            "designed", "developed", "devised", "engineered", "enhanced", "established", "executed",
            "expanded", "formulated", "generated", "headed", "implemented", "improved", "increased",
            "initiated", "instituted", "integrated", "launched", "led", "maximized", "mentored",
            "migrated", "minimized", "modernized", "negotiated", "optimized", "orchestrated", "overhauled",
            "pioneered", "reduced", "refactored", "resolved", "revamped", "scaled", "simplified",
            "spearheaded", "standardized", "streamlined", "strengthened", "surpassed", "transformed", "upgraded"
    );

    private static final Pattern QUANTIFIABLE_PATTERN = Pattern.compile(
            "(\\d+([.,]\\d+)?%|\\$\\d+([.,]\\d+)?[kKmMbB]?|\\b\\d+\\+?\\s*(users|clients|requests|ms|seconds|minutes|hours|days|teams|microservices|services|engineers|developers|nodes|containers|servers|endpoints|queries|transactions|records|terabytes|gigabytes)\\b)",
            Pattern.CASE_INSENSITIVE
    );

    public String extractText(MultipartFile file) throws IOException {
        String filename = file.getOriginalFilename() != null ? file.getOriginalFilename().toLowerCase() : "";
        if (filename.endsWith(".pdf")) {
            byte[] bytes = file.getBytes();
            try (PDDocument document = Loader.loadPDF(bytes)) {
                PDFTextStripper stripper = new PDFTextStripper();
                return stripper.getText(document);
            }
        } else {
            // Assume plain text / markdown
            return new String(file.getBytes(), StandardCharsets.UTF_8);
        }
    }

    public Map<String, String> extractSections(String text) {
        Map<String, String> sections = new LinkedHashMap<>();
        if (text == null || text.isBlank()) {
            return sections;
        }

        String[] knownHeaders = {
                "SUMMARY", "PROFESSIONAL SUMMARY", "OBJECTIVE",
                "SKILLS", "TECHNICAL SKILLS", "CORE COMPETENCIES",
                "EXPERIENCE", "PROFESSIONAL EXPERIENCE", "WORK EXPERIENCE", "EMPLOYMENT HISTORY",
                "EDUCATION", "ACADEMIC BACKGROUND",
                "PROJECTS", "KEY PROJECTS", "PERSONAL PROJECTS",
                "CERTIFICATIONS", "LICENSES & CERTIFICATIONS"
        };

        // Create a regex to match common resume section headers
        StringBuilder headerRegex = new StringBuilder("(?im)^\\s*(");
        for (int i = 0; i < knownHeaders.length; i++) {
            headerRegex.append(Pattern.quote(knownHeaders[i]));
            if (i < knownHeaders.length - 1) headerRegex.append("|");
        }
        headerRegex.append(")\\s*[:\\-]?\\s*$");

        Pattern pattern = Pattern.compile(headerRegex.toString());
        Matcher matcher = pattern.matcher(text);

        List<Integer> headerPositions = new ArrayList<>();
        List<String> headerNames = new ArrayList<>();

        while (matcher.find()) {
            headerPositions.add(matcher.start());
            headerNames.add(matcher.group(1).toUpperCase().trim());
        }

        if (headerPositions.isEmpty()) {
            // If no explicit standard headers matched, partition into Summary & General Content
            sections.put("CONTENT", text.trim());
            return sections;
        }

        // Add leading content before first header as Contact / Intro
        if (headerPositions.get(0) > 0) {
            String intro = text.substring(0, headerPositions.get(0)).trim();
            if (!intro.isEmpty()) {
                sections.put("CONTACT / INTRO", intro);
            }
        }

        for (int i = 0; i < headerPositions.size(); i++) {
            int start = headerPositions.get(i);
            int end = (i + 1 < headerPositions.size()) ? headerPositions.get(i + 1) : text.length();
            String sectionTitle = headerNames.get(i);
            String content = text.substring(start, end).trim();
            
            // Remove the header line itself from section content
            int firstNewline = content.indexOf('\n');
            if (firstNewline != -1) {
                content = content.substring(firstNewline + 1).trim();
            }

            sections.put(normalizeSectionName(sectionTitle), content);
        }

        return sections;
    }

    private String normalizeSectionName(String raw) {
        if (raw.contains("SKILL")) return "SKILLS";
        if (raw.contains("EXPERIENCE") || raw.contains("EMPLOYMENT")) return "EXPERIENCE";
        if (raw.contains("EDUCATION") || raw.contains("ACADEMIC")) return "EDUCATION";
        if (raw.contains("PROJECT")) return "PROJECTS";
        if (raw.contains("CERTIFICATION")) return "CERTIFICATIONS";
        if (raw.contains("SUMMARY") || raw.contains("OBJECTIVE")) return "SUMMARY";
        return raw;
    }

    public List<String> extractSkills(String text) {
        if (text == null || text.isBlank()) return Collections.emptyList();
        String lower = " " + text.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9#+.]", " ") + " ";
        Set<String> matched = new LinkedHashSet<>();

        for (String skill : TECH_DICTIONARY) {
            String searchPattern = "\\b" + Pattern.quote(skill) + "\\b";
            if (Pattern.compile(searchPattern, Pattern.CASE_INSENSITIVE).matcher(lower).find()) {
                matched.add(capitalizeSkill(skill));
            }
        }

        return new ArrayList<>(matched);
    }

    public int countActionVerbs(String text) {
        if (text == null || text.isBlank()) return 0;
        String lower = text.toLowerCase(Locale.ROOT);
        int count = 0;
        for (String verb : ACTION_VERBS) {
            Pattern p = Pattern.compile("\\b" + Pattern.quote(verb) + "\\b", Pattern.CASE_INSENSITIVE);
            Matcher m = p.matcher(lower);
            while (m.find()) {
                count++;
            }
        }
        return count;
    }

    public int countQuantifiableMetrics(String text) {
        if (text == null || text.isBlank()) return 0;
        Matcher matcher = QUANTIFIABLE_PATTERN.matcher(text);
        int count = 0;
        while (matcher.find()) {
            count++;
        }
        return count;
    }

    public int countWords(String text) {
        if (text == null || text.isBlank()) return 0;
        return text.trim().split("\\s+").length;
    }

    private String capitalizeSkill(String skill) {
        if (skill.equalsIgnoreCase("aws") || skill.equalsIgnoreCase("gcp") || skill.equalsIgnoreCase("ci/cd") || skill.equalsIgnoreCase("rag") || skill.equalsIgnoreCase("llm")) {
            return skill.toUpperCase();
        }
        if (skill.equalsIgnoreCase("next.js") || skill.equalsIgnoreCase("node.js")) {
            return skill.substring(0, 1).toUpperCase() + skill.substring(1);
        }
        String[] words = skill.split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String w : words) {
            if (!w.isEmpty()) {
                sb.append(Character.toUpperCase(w.charAt(0))).append(w.substring(1).toLowerCase()).append(" ");
            }
        }
        return sb.toString().trim();
    }
}
