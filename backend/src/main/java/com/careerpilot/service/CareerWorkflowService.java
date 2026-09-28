package com.careerpilot.service;

import com.careerpilot.entity.AbilityAnalysis;
import com.careerpilot.entity.CareerPlan;
import com.careerpilot.entity.UserProfile;

public interface CareerWorkflowService {

    CareerPlan generatePlan(Long userId, String stage, String targetPosition);

    AbilityAnalysis analyzeAbility(Long userId, String targetPosition);

    CareerPlan generatePlanFromProfile(UserProfile profile);

    AbilityAnalysis analyzeAbilityFromProfile(UserProfile profile);
}
