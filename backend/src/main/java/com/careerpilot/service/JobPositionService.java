package com.careerpilot.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.careerpilot.entity.JobPosition;
import com.careerpilot.vo.JobMatchVO;

import java.util.List;

public interface JobPositionService extends IService<JobPosition> {

    List<JobPosition> filterJobs(String keyword, String category, String city, String jobType, String techStack);

    List<JobMatchVO> matchJobsForUser(Long userId);
}
