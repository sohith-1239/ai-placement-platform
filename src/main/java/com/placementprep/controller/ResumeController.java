package com.placementprep.controller;

import com.placementprep.entity.Resume;
import com.placementprep.entity.User;
import com.placementprep.repository.ResumeRepository;
import com.placementprep.repository.UserRepository;
import com.placementprep.security.UserDetailsImpl;
import com.placementprep.service.ResumeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.List;
import java.util.Collections;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/resume")
public class ResumeController {

    @Autowired
    ResumeService resumeService;
    
    @Autowired
    ResumeRepository resumeRepository;
    
    @Autowired
    UserRepository userRepository;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadResume(@RequestParam("file") MultipartFile file) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            User user = userRepository.findById(userDetails.getId()).orElse(null);
            
            String text = resumeService.extractTextFromPDF(file);
            String aiResponse = resumeService.analyzeResume(text);
            
            String jsonStr = aiResponse;
            Matcher m = Pattern.compile("\\{.*\\}", Pattern.DOTALL).matcher(aiResponse);
            if (m.find()) {
                jsonStr = m.group(0);
            }
            
            int score = 75;
            java.util.regex.Matcher scoreMatcher = java.util.regex.Pattern.compile("\"score\"\\s*:\\s*(\\d+)").matcher(jsonStr);
            if (scoreMatcher.find()) {
                try { score = Integer.parseInt(scoreMatcher.group(1)); } catch(Exception e) {}
            }
            
            Resume resume = new Resume(user, "local_mock_url", score, aiResponse);
            resumeRepository.save(resume);
            
            return ResponseEntity.ok(Map.of("score", score, "feedback", aiResponse));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error parsing resume: " + e.getMessage());
        }
    }

    @GetMapping("/score")
    public ResponseEntity<?> getScores() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        
        List<Resume> resumes = resumeRepository.findByUserId(userDetails.getId());
        if(resumes.isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList());
        }
        return ResponseEntity.ok(resumes);
    }
}
