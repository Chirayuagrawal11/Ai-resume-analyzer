package com.chirayu.resumeanalyzer.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name="jobs")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Job {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional=false)
    private User user;

    @Column(nullable=false)
    private String title;

    private String company;

    @Column(columnDefinition="TEXT", nullable=false)
    private String description;

    private LocalDateTime createdAt;
}
