package com.placementprep.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "saved_jobs")
@Data
@NoArgsConstructor
public class SavedJob {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String jobTitle;
    private String companyName;

    public SavedJob(User user, String jobTitle, String companyName) {
        this.user = user;
        this.jobTitle = jobTitle;
        this.companyName = companyName;
    }
}
