package com.careerpilot.controller;

import com.careerpilot.common.Result;
import com.careerpilot.entity.JobPosition;
import com.careerpilot.service.JobPositionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
public class JobPositionController {

    private final JobPositionService jobPositionService;

    public JobPositionController(JobPositionService jobPositionService) {
        this.jobPositionService = jobPositionService;
    }

    @GetMapping
    public Result<List<JobPosition>> list(@RequestParam(name = "keyword", required = false) String keyword,
                                          @RequestParam(name = "category", required = false) String category,
                                          @RequestParam(name = "city", required = false) String city,
                                          @RequestParam(name = "jobType", required = false) String jobType,
                                          @RequestParam(name = "techStack", required = false) String techStack) {
        return Result.success(jobPositionService.filterJobs(keyword, category, city, jobType, techStack));
    }

    @GetMapping("/{id}")
    public Result<JobPosition> detail(@PathVariable Long id) {
        return Result.success(jobPositionService.getById(id));
    }
}
