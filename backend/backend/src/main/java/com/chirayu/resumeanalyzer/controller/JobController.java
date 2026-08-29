package com.chirayu.resumeanalyzer.controller;

import com.chirayu.resumeanalyzer.dto.JobMatchResponse;
import com.chirayu.resumeanalyzer.model.Job;
import com.chirayu.resumeanalyzer.service.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/jobs")
public class JobController {
    private final JobService jobs;
    private final ResumeService resumes;
    private final AIService ai;

    public JobController(JobService jobs,ResumeService resumes,AIService ai){
        this.jobs=jobs;this.resumes=resumes;this.ai=ai;
    }

    public record JobRequest(String title,String company,String description){}

    @PostMapping
    public Job create(Authentication auth,@RequestBody JobRequest request){
        return jobs.create(auth.getName(),request.title(),request.company(),request.description());
    }

    @GetMapping
    public List<Job> mine(Authentication auth){return jobs.myJobs(auth.getName());}

    @PostMapping("/match/{resumeId}")
    public JobMatchResponse match(Authentication auth,@PathVariable Long resumeId,
                                  @RequestBody JobRequest request){
        var resume=resumes.getOwnedResume(auth.getName(),resumeId);
        return ai.match(resume.getExtractedText(),request.description());
    }
}
