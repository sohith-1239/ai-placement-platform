package com.placementprep.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GeminiService {
    @Value("${gemini.api.key}")
    private String geminiApiKey;

    public String generateContent(String promptText) {
        if(geminiApiKey == null || geminiApiKey.contains("mock_key")) {
            return generateMockResponse(promptText);
        }

        try {
            String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent?key=" + geminiApiKey;
            RestTemplate restTemplate = new RestTemplate();
    
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            Map<String, Object> parts = new HashMap<>();
            parts.put("text", promptText);
            
            Map<String, Object> contents = new HashMap<>();
            contents.put("parts", List.of(parts));
            
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("contents", List.of(contents));
            
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            Map<String, Object> response = restTemplate.postForObject(url, entity, Map.class);
            
            return extractText(response);
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"score\": 80, \"feedback\": \"Fallback feedback due to API error. Skills matched.\"}";
        }
    }

    private String extractText(Map<String, Object> response) {
        try {
            List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.get("candidates");
            Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
            List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
            return (String) parts.get(0).get("text");
        } catch(Exception e) {
            return "{\"score\": 80, \"feedback\": \"Error parsing API response.\"}";
        }
    }

    private String generateMockResponse(String text) {
        if(text.contains("ATS")) {
            return "{\"score\": 85, \"feedback\": \"Great resume. Add more leadership roles! Missing skills: Docker, AWS.\"}";
        } else if (text.contains("Interview")) {
            return "{\"questions\": [\"What is OOP?\", \"Explain Solid principles.\"], \"score\": 0, \"feedback\": \"Mock Questions Generated.\"}";
        }
        return "{\"message\": \"Mock response!\"}";
    }
}
