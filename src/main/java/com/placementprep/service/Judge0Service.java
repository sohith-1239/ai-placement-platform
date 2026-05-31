package com.placementprep.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpMethod;

import java.util.HashMap;
import java.util.Map;

@Service
public class Judge0Service {
    @Value("${judge0.api.key}")
    private String judge0ApiKey;

    @Value("${judge0.api.host}")
    private String judge0ApiHost;

    public Map<String, Object> runCode(String sourceCode, int languageId) {
        if(judge0ApiKey == null || judge0ApiKey.contains("mock_key")) {
            return Map.of("stdout", "Mock Output:\nHello World", "status", Map.of("description", "Accepted"));
        }

        try {
            String url = "https://" + judge0ApiHost + "/submissions?base64_encoded=false&wait=true";
            RestTemplate restTemplate = new RestTemplate();
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-RapidAPI-Key", judge0ApiKey);
            headers.set("X-RapidAPI-Host", judge0ApiHost);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("source_code", sourceCode);
            requestBody.put("language_id", languageId);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);
            return response.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            return Map.of("stderr", "Compilation Error or API Error", "status", Map.of("description", "Error"));
        }
    }
}
