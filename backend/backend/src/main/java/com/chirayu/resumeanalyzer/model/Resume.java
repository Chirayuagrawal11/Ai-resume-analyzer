package com.chirayu.resumeanalyzer.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name="resumes")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Resume {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional=false)
    private User user;

    @Column(nullable=false)
    private String fileName;

    @Column(columnDefinition="TEXT")
    private String extractedText;

    private LocalDateTime uploadedAt;
}
