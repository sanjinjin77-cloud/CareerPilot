package com.careerpilot.controller;

import com.careerpilot.common.Result;
import com.careerpilot.service.AuthSessionService;
import com.careerpilot.service.JobPositionService;
import com.careerpilot.vo.JobMatchVO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/career")
public class CareerController {

    private final JobPositionService jobPositionService;
    private final AuthSessionService authSessionService;

    public CareerController(JobPositionService jobPositionService, AuthSessionService authSessionService) {
        this.jobPositionService = jobPositionService;
        this.authSessionService = authSessionService;
    }

    @GetMapping("/job-match")
    public Result<List<JobMatchVO>> matchJobs(HttpServletRequest request) {
        return Result.success(jobPositionService.matchJobsForUser(authSessionService.currentUserId(request)));
    }
}
