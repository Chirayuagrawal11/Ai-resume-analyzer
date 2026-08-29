package com.chirayu.resumeanalyzer.service;

import com.chirayu.resumeanalyzer.dto.AnalysisResponse;
import com.chirayu.resumeanalyzer.dto.JobMatchResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Service
public class AIService {

    private final RestClient client;
    private final ObjectMapper mapper;
    private final String apiKey;
    private final String model;

    public AIService(
            @Value("${ai.api-url}") String url,
            @Value("${ai.api-key}") String apiKey,
            @Value("${ai.model}") String model,
            ObjectMapper mapper
    ) {
        this.client = RestClient.builder()
                .baseUrl(url)
                .build();

        this.apiKey = apiKey;
        this.model = model;
        this.mapper = mapper;
    }

    public AnalysisResponse analyzeResume(String resumeText) {

        checkApiKey();

        String prompt = """
                Analyze the following resume.

                Return ONLY valid JSON.
                Do not use markdown.
                Do not use ```json.

                Required JSON format:
                {
                  "score": 0,
                  "skills": [],
                  "strengths": [],
                  "weaknesses": [],
                  "missingSkills": [],
                  "suggestions": []
                }

                Rules:
                - score must be an integer from 0 to 100
                - all arrays must contain strings
                - keep suggestions practical for a student/job seeker

                Resume:
                %s
                """.formatted(limit(resumeText, 12000));

        String json = generateContent(prompt);

        try {
            return mapper.readValue(
                    cleanJson(json),
                    AnalysisResponse.class
            );
        } catch (Exception e) {
            throw new IllegalStateException(
                    "Gemini returned invalid analysis JSON: " + e.getMessage()
            );
        }
    }

    public JobMatchResponse match(
            String resumeText,
            String jobDescription
    ) {

        checkApiKey();

        String prompt = """
                Compare the resume with the job description.

                Return ONLY valid JSON.
                Do not use markdown.
                Do not use ```json.

                Required JSON format:
                {
                  "matchScore": 0,
                  "matchedSkills": [],
                  "missingSkills": [],
                  "recommendation": ""
                }

                Rules:
                - matchScore must be an integer from 0 to 100
                - arrays must contain strings

                Resume:
                %s

                Job Description:
                %s
                """.formatted(
                limit(resumeText, 10000),
                limit(jobDescription, 8000)
        );

        String json = generateContent(prompt);

        try {
            return mapper.readValue(
                    cleanJson(json),
                    JobMatchResponse.class
            );
        } catch (Exception e) {
            throw new IllegalStateException(
                    "Gemini returned invalid matching JSON: " + e.getMessage()
            );
        }
    }

    private String generateContent(String prompt) {

        Map<String, Object> body = Map.of(
                "contents", List.of(
                        Map.of(
                                "parts", List.of(
                                        Map.of(
                                                "text",
                                                prompt
                                        )
                                )
                        )
                ),
                "generationConfig", Map.of(
                        "temperature",
                        0.2
                )
        );

        Map<?, ?> response = client.post()
                .uri(uriBuilder ->
                        uriBuilder
                                .path("/" + model + ":generateContent")
                                .queryParam("key", apiKey)
                                .build()
                )
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .body(Map.class);

        if (response == null) {
            throw new IllegalStateException(
                    "Empty response from Gemini"
            );
        }

        Object candidatesObject =
                response.get("candidates");

        if (!(candidatesObject instanceof List<?> candidates)
                || candidates.isEmpty()) {

            throw new IllegalStateException(
                    "Gemini returned no candidates: " + response
            );
        }

        Object firstObject = candidates.get(0);

        if (!(firstObject instanceof Map<?, ?> firstCandidate)) {
            throw new IllegalStateException(
                    "Invalid Gemini candidate response"
            );
        }

        Object contentObject =
                firstCandidate.get("content");

        if (!(contentObject instanceof Map<?, ?> content)) {
            throw new IllegalStateException(
                    "Gemini response content missing"
            );
        }

        Object partsObject =
                content.get("parts");

        if (!(partsObject instanceof List<?> parts)
                || parts.isEmpty()) {

            throw new IllegalStateException(
                    "Gemini response parts missing"
            );
        }

        Object firstPartObject = parts.get(0);

        if (!(firstPartObject instanceof Map<?, ?> firstPart)) {
            throw new IllegalStateException(
                    "Invalid Gemini response part"
            );
        }

        Object text = firstPart.get("text");

        if (text == null) {
            throw new IllegalStateException(
                    "Gemini response text missing"
            );
        }

        return String.valueOf(text);
    }

    private void checkApiKey() {

        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException(
                    "GEMINI_API_KEY is not configured"
            );
        }
    }

    private String cleanJson(String json) {

        if (json == null) {
            return "";
        }

        return json
                .replace("```json", "")
                .replace("```", "")
                .trim();
    }

    private String limit(String value, int max) {

        if (value == null) {
            return "";
        }

        return value.substring(
                0,
                Math.min(value.length(), max)
        );
    }
}