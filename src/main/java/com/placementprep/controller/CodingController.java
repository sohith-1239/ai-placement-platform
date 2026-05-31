package com.placementprep.controller;

import com.placementprep.entity.CodingSubmission;
import com.placementprep.entity.User;
import com.placementprep.repository.CodingSubmissionRepository;
import com.placementprep.repository.UserRepository;
import com.placementprep.security.UserDetailsImpl;
import com.placementprep.service.Judge0Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/code")
public class CodingController {

    @Autowired
    Judge0Service judge0Service;
    
    @Autowired
    CodingSubmissionRepository codingSubmissionRepository;
    
    @Autowired
    UserRepository userRepository;

    @PostMapping("/run")
    public ResponseEntity<?> runCode(@RequestBody Map<String, Object> request) {
        String sourceCode = (String) request.get("sourceCode");
        int languageId = (Integer) request.getOrDefault("languageId", 62); // Java default
        
        Map<String, Object> result = judge0Service.runCode(sourceCode, languageId);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/submit")
    public ResponseEntity<?> submitCode(@RequestBody Map<String, Object> request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User user = userRepository.findById(userDetails.getId()).orElse(null);

        String sourceCode = (String) request.get("sourceCode");
        int languageId = (Integer) request.getOrDefault("languageId", 62); 
        String language = (String) request.getOrDefault("language", "Java");

        Map<String, Object> result = judge0Service.runCode(sourceCode, languageId);
        String statusDescription = "Unknown";
        if(result.containsKey("status")) {
            Map<String, Object> statusObj = (Map<String, Object>) result.get("status");
            statusDescription = (String) statusObj.get("description");
        }
        
        CodingSubmission submission = new CodingSubmission(user, language, sourceCode, statusDescription);
        codingSubmissionRepository.save(submission);
        
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/history")
    public ResponseEntity<?> getHistory() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return ResponseEntity.ok(codingSubmissionRepository.findByUserId(userDetails.getId()));
    }
}
