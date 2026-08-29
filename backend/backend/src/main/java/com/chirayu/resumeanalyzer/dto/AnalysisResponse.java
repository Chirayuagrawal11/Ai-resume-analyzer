package com.chirayu.resumeanalyzer.dto;

import lombok.Data;
import java.util.List;

@Data
public class AnalysisResponse {
    private Integer score;
    private List<String> skills;
    private List<String> strengths;
    private List<String> weaknesses;
    private List<String> missingSkills;
    private List<String> suggestions;
}
