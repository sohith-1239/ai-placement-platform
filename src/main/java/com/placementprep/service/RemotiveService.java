package com.placementprep.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class RemotiveService {
    @Value("${remotive.api.url}")
    private String remotiveUrl;

    public Map<String, Object> searchJobs(String limit) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            return restTemplate.getForObject(remotiveUrl + "?limit=" + limit, Map.class);
        } catch (Exception e) {
            e.printStackTrace();
            return Map.of("error", "Could not fetch jobs");
        }
    }
}
