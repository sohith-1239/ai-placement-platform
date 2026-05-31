package com.placementprep.controller;

import com.placementprep.repository.*;
import com.placementprep.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.HashMap;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    ResumeRepository resumeRepository;
    @Autowired
    InterviewRepository interviewRepository;
    @Autowired
    CodingSubmissionRepository codingSubmissionRepository;
    @Autowired
    SavedJobRepository savedJobRepository;
    @Autowired
    GitHubProfileRepository githubProfileRepository;

    @GetMapping
    public ResponseEntity<?> getDashboardStats() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Long userId = userDetails.getId();

        int totalInterviews = interviewRepository.findByUserId(userId).size();
        int codingProblemsSolved = codingSubmissionRepository.findByUserId(userId).size();
        int savedJobsCount = savedJobRepository.findByUserId(userId).size();
        
        int bestResumeScore = resumeRepository.findByUserId(userId).stream()
                .mapToInt(r -> r.getAtsScore())
                .max().orElse(0);

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalInterviews", totalInterviews);
        stats.put("codingProblemsSolved", codingProblemsSolved);
        stats.put("savedJobsCount", savedJobsCount);
        stats.put("bestResumeScore", bestResumeScore);
        
        githubProfileRepository.findByUserId(userId).ifPresent(p -> stats.put("githubProfile", p));

        return ResponseEntity.ok(stats);
    }
}
