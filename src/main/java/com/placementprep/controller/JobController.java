package com.placementprep.controller;

import com.placementprep.entity.SavedJob;
import com.placementprep.entity.User;
import com.placementprep.repository.SavedJobRepository;
import com.placementprep.repository.UserRepository;
import com.placementprep.security.UserDetailsImpl;
import com.placementprep.service.RemotiveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/jobs")
public class JobController {

    @Autowired
    RemotiveService remotiveService;

    @Autowired
    SavedJobRepository savedJobRepository;
    
    @Autowired
    UserRepository userRepository;

    @GetMapping("/search")
    public ResponseEntity<?> searchJobs(@RequestParam(defaultValue = "10") String limit) {
        return ResponseEntity.ok(remotiveService.searchJobs(limit));
    }

    @PostMapping("/save")
    public ResponseEntity<?> saveJob(@RequestBody Map<String, String> request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User user = userRepository.findById(userDetails.getId()).orElse(null);

        String jobTitle = request.get("jobTitle");
        String companyName = request.get("companyName");

        SavedJob savedJob = new SavedJob(user, jobTitle, companyName);
        savedJobRepository.save(savedJob);

        return ResponseEntity.ok(Map.of("message", "Job saved successfully"));
    }
}
