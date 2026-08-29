package com.chirayu.resumeanalyzer.service;

import com.chirayu.resumeanalyzer.model.Job;
import com.chirayu.resumeanalyzer.model.User;
import com.chirayu.resumeanalyzer.repository.JobRepository;
import com.chirayu.resumeanalyzer.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class JobService {
    private final JobRepository jobs;
    private final UserRepository users;

    public JobService(JobRepository jobs,UserRepository users){this.jobs=jobs;this.users=users;}

    public Job create(String email,String title,String company,String description){
        User u=users.findByEmail(email).orElseThrow();
        return jobs.save(Job.builder().user(u).title(title).company(company)
                .description(description).createdAt(LocalDateTime.now()).build());
    }

    public List<Job> myJobs(String email){
        User u=users.findByEmail(email).orElseThrow();
        return jobs.findByUserIdOrderByCreatedAtDesc(u.getId());
    }
}
