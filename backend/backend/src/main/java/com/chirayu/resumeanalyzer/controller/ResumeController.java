package com.chirayu.resumeanalyzer.controller;

import com.chirayu.resumeanalyzer.model.Resume;
import com.chirayu.resumeanalyzer.model.ResumeAnalysis;
import com.chirayu.resumeanalyzer.service.ResumeService;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequestMapping("/api/resumes")
public class ResumeController {
    private final ResumeService service;
    public ResumeController(ResumeService service){this.service=service;}

    @PostMapping(value="/upload",consumes=MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResumeAnalysis upload(Authentication auth,@RequestParam("file") MultipartFile file){
        return service.uploadAndAnalyze(auth.getName(),file);
    }

    @GetMapping
    public List<Resume> mine(Authentication auth){return service.myResumes(auth.getName());}

    @GetMapping("/{id}/analysis")
    public ResumeAnalysis analysis(Authentication auth,@PathVariable Long id){
        return service.getAnalysis(auth.getName(),id);
    }
}
