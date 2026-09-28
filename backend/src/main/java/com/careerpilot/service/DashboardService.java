package com.careerpilot.service;

import com.careerpilot.dto.DashboardSnapshot;

public interface DashboardService {

    DashboardSnapshot getSnapshot(Long userId);
}
