package com.chirayu.resumeanalyzer.dto;

import lombok.Data;
import java.util.List;

@Data
public class JobMatchResponse {
    private Integer matchScore;
    private List<String> matchedSkills;
    private List<String> missingSkills;
    private String recommendation;
}
