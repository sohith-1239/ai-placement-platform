package com.placementprep.controller;

import com.placementprep.entity.Interview;
import com.placementprep.entity.User;
import com.placementprep.repository.InterviewRepository;
import com.placementprep.repository.UserRepository;
import com.placementprep.security.UserDetailsImpl;
import com.placementprep.service.GeminiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.List;
import java.util.Collections;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/interview")
public class InterviewController {

    @Autowired
    GeminiService geminiService;
    
    @Autowired
    InterviewRepository interviewRepository;
    
    @Autowired
    UserRepository userRepository;

    @PostMapping("/generate")
    public ResponseEntity<?> generateQuestions(@RequestBody Map<String, String> request) {
        String jobRole = request.getOrDefault("role", "Software Engineer");
        String prompt = "Generate 5 technical interview questions for a " + jobRole + " fresher role. Format as a JSON list of strings under key 'questions'.";
        
        String response = geminiService.generateContent(prompt);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/evaluate")
    public ResponseEntity<?> evaluateInterview(@RequestBody Map<String, Object> request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User user = userRepository.findById(userDetails.getId()).orElse(null);

        String role = (String) request.getOrDefault("role", "Software Engineer");
        String answers = (String) request.getOrDefault("answers", "No answers provided");
        
        String prompt = "Evaluate the following interview answers for a " + role + " role. " +
                        "Return a JSON object with 'score' (0-100) and 'feedback' string. Answers: " + answers;
        
        String aiResponse = geminiService.generateContent(prompt);
        
        int score = 60;
        java.util.regex.Matcher scoreMatcher = java.util.regex.Pattern.compile("\"score\"\\s*:\\s*(\\d+)").matcher(aiResponse);
        if (scoreMatcher.find()) {
            try { score = Integer.parseInt(scoreMatcher.group(1)); } catch(Exception e) {}
        }
        
        Interview interview = new Interview(user, role, score, aiResponse);
        interviewRepository.save(interview);
        
        return ResponseEntity.ok(Map.of("score", score, "feedback", aiResponse));
    }
}
