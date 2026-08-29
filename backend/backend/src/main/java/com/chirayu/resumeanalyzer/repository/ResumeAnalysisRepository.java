package com.chirayu.resumeanalyzer.repository;

import com.chirayu.resumeanalyzer.model.ResumeAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ResumeAnalysisRepository extends JpaRepository<ResumeAnalysis,Long> {
    Optional<ResumeAnalysis> findByResumeId(Long resumeId);
}
