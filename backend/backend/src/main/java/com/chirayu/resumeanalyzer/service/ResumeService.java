package com.chirayu.resumeanalyzer.service;

import com.chirayu.resumeanalyzer.dto.*;
import com.chirayu.resumeanalyzer.model.*;
import com.chirayu.resumeanalyzer.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ResumeService {
    private final UserRepository users;
    private final ResumeRepository resumes;
    private final ResumeAnalysisRepository analyses;
    private final PdfService pdf;
    private final AIService ai;
    private final ObjectMapper mapper;

    public ResumeService(UserRepository users, ResumeRepository resumes,
                         ResumeAnalysisRepository analyses, PdfService pdf,
                         AIService ai, ObjectMapper mapper) {
        this.users=users; this.resumes=resumes; this.analyses=analyses;
        this.pdf=pdf; this.ai=ai; this.mapper=mapper;
    }

    public ResumeAnalysis uploadAndAnalyze(String email, MultipartFile file) {
        User user=users.findByEmail(email).orElseThrow();
        String text=pdf.extractText(file);

        Resume resume=resumes.save(Resume.builder()
                .user(user).fileName(file.getOriginalFilename())
                .extractedText(text).uploadedAt(LocalDateTime.now()).build());

        AnalysisResponse dto=ai.analyzeResume(text);

        try {
            ResumeAnalysis a=ResumeAnalysis.builder()
                .resume(resume).score(dto.getScore())
                .skillsJson(mapper.writeValueAsString(dto.getSkills()))
                .strengthsJson(mapper.writeValueAsString(dto.getStrengths()))
                .weaknessesJson(mapper.writeValueAsString(dto.getWeaknesses()))
                .missingSkillsJson(mapper.writeValueAsString(dto.getMissingSkills()))
                .suggestionsJson(mapper.writeValueAsString(dto.getSuggestions()))
                .createdAt(LocalDateTime.now()).build();
            return analyses.save(a);
        } catch(Exception e) {
            throw new IllegalStateException("Could not save analysis",e);
        }
    }

    public List<Resume> myResumes(String email) {
        User u=users.findByEmail(email).orElseThrow();
        return resumes.findByUserIdOrderByUploadedAtDesc(u.getId());
    }

    public Resume getOwnedResume(String email,Long id) {
        User u=users.findByEmail(email).orElseThrow();
        Resume r=resumes.findById(id).orElseThrow();
        if(!r.getUser().getId().equals(u.getId())) throw new SecurityException("Access denied");
        return r;
    }

    public ResumeAnalysis getAnalysis(String email,Long resumeId) {
        getOwnedResume(email,resumeId);
        return analyses.findByResumeId(resumeId).orElseThrow();
    }
}
