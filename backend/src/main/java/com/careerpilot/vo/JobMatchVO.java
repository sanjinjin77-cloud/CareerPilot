package com.careerpilot.vo;

import com.careerpilot.entity.JobPosition;

import java.util.List;

public class JobMatchVO {

    private JobPosition job;
    private Integer matchScore;
    private List<String> matchReasons;

    public JobPosition getJob() {
        return job;
    }

    public void setJob(JobPosition job) {
        this.job = job;
    }

    public Integer getMatchScore() {
        return matchScore;
    }

    public void setMatchScore(Integer matchScore) {
        this.matchScore = matchScore;
    }

    public List<String> getMatchReasons() {
        return matchReasons;
    }

    public void setMatchReasons(List<String> matchReasons) {
        this.matchReasons = matchReasons;
    }
}
