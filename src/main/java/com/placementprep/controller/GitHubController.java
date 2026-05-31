package com.placementprep.controller;

import com.placementprep.entity.GitHubProfile;
import com.placementprep.entity.User;
import com.placementprep.repository.GitHubProfileRepository;
import com.placementprep.repository.UserRepository;
import com.placementprep.security.UserDetailsImpl;
import com.placementprep.service.GitHubService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/github")
public class GitHubController {

    @Autowired
    GitHubService githubService;
    
    @Autowired
    GitHubProfileRepository githubProfileRepository;

    @Autowired
    UserRepository userRepository;

    @GetMapping("/{username}")
    public ResponseEntity<?> analyzeGitHub(@PathVariable String username) {
        Map<String, Object> analysis = githubService.fetchProfileInfo(username);
        
        if(!analysis.containsKey("error")) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            User user = userRepository.findById(userDetails.getId()).orElse(null);

            Integer repos = (Integer) analysis.get("repositories");
            String topLang = (String) analysis.get("topLanguage");

            GitHubProfile profile = githubProfileRepository.findByUserId(user.getId())
                                        .orElse(new GitHubProfile(user, username, repos, topLang));
            
            profile.setGithubUsername(username);
            profile.setRepositories(repos);
            profile.setTopLanguage(topLang);
            
            githubProfileRepository.save(profile);
        }
        
        return ResponseEntity.ok(analysis);
    }
}
