package com.placementprep.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "resumes")
@Data
@NoArgsConstructor
public class Resume {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String resumeUrl; // Local or cloud url
    private Integer atsScore;
    
    @Column(columnDefinition="TEXT")
    private String feedback;

    public Resume(User user, String resumeUrl, Integer atsScore, String feedback) {
        this.user = user;
        this.resumeUrl = resumeUrl;
        this.atsScore = atsScore;
        this.feedback = feedback;
    }
}
