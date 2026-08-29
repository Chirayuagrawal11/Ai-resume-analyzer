package com.chirayu.resumeanalyzer.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name="resume_analysis")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ResumeAnalysis {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional=false)
    private Resume resume;

    private Integer score;

    @Column(columnDefinition="TEXT")
    private String skillsJson;

    @Column(columnDefinition="TEXT")
    private String strengthsJson;

    @Column(columnDefinition="TEXT")
    private String weaknessesJson;

    @Column(columnDefinition="TEXT")
    private String missingSkillsJson;

    @Column(columnDefinition="TEXT")
    private String suggestionsJson;

    private LocalDateTime createdAt;
}
