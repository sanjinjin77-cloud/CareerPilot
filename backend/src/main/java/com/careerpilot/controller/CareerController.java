package com.careerpilot.controller;

import com.careerpilot.common.Result;
import com.careerpilot.service.JobPositionService;
import com.careerpilot.vo.JobMatchVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/career")
public class CareerController {

    private final JobPositionService jobPositionService;

    public CareerController(JobPositionService jobPositionService) {
        this.jobPositionService = jobPositionService;
    }

    @GetMapping("/job-match")
    public Result<List<JobMatchVO>> matchJobs(@RequestParam(name = "userId", required = false, defaultValue = "1") Long userId) {
        return Result.success(jobPositionService.matchJobsForUser(userId));
    }
}
