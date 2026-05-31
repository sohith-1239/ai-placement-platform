package com.placementprep.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "coding_submissions")
@Data
@NoArgsConstructor
public class CodingSubmission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String language;
    
    @Column(columnDefinition="TEXT")
    private String sourceCode;
    
    private String status;

    public CodingSubmission(User user, String language, String sourceCode, String status) {
        this.user = user;
        this.language = language;
        this.sourceCode = sourceCode;
        this.status = status;
    }
}
