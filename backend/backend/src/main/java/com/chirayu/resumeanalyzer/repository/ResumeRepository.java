package com.chirayu.resumeanalyzer.repository;

import com.chirayu.resumeanalyzer.model.Resume;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ResumeRepository extends JpaRepository<Resume,Long> {
    List<Resume> findByUserIdOrderByUploadedAtDesc(Long userId);
}
