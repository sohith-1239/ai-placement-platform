package com.placementprep.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "interviews")
@Data
@NoArgsConstructor
public class Interview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String interviewType;
    private Integer score;
    
    @Column(columnDefinition="TEXT")
    private String feedback;

    public Interview(User user, String interviewType, Integer score, String feedback) {
        this.user = user;
        this.interviewType = interviewType;
        this.score = score;
        this.feedback = feedback;
    }
}
