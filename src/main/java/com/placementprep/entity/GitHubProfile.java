package com.placementprep.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "github_profiles")
@Data
@NoArgsConstructor
public class GitHubProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String githubUsername;
    private Integer repositories;
    private String topLanguage;

    public GitHubProfile(User user, String githubUsername, Integer repositories, String topLanguage) {
        this.user = user;
        this.githubUsername = githubUsername;
        this.repositories = repositories;
        this.topLanguage = topLanguage;
    }
}
