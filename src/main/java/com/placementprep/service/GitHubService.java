package com.placementprep.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.List;
import java.util.HashMap;

@Service
public class GitHubService {

    public Map<String, Object> fetchProfileInfo(String username) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String userUrl = "https://api.github.com/users/" + username;
            Map userResponse = restTemplate.getForObject(userUrl, Map.class);
            
            String reposUrl = "https://api.github.com/users/" + username + "/repos";
            Object[] repos = restTemplate.getForObject(reposUrl, Object[].class);
            
            Map<String, Integer> languages = new HashMap<>();
            String topLanguage = "None";
            int max = 0;
            
            for(Object r : repos) {
                Map<String, Object> repo = (Map<String, Object>) r;
                String lang = (String) repo.get("language");
                if(lang != null) {
                    languages.put(lang, languages.getOrDefault(lang, 0) + 1);
                    if(languages.get(lang) > max) {
                        max = languages.get(lang);
                        topLanguage = lang;
                    }
                }
            }
            
            Integer publicRepos = (Integer) userResponse.get("public_repos");
            
            Map<String, Object> result = new HashMap<>();
            result.put("username", username);
            result.put("repositories", publicRepos);
            result.put("topLanguage", topLanguage);
            result.put("languagesDistribution", languages);
            
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return Map.of("error", "Error fetching github data");
        }
    }
}
