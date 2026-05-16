package com.careerpilot.vo;

import com.careerpilot.entity.AbilityAnalysis;
import com.careerpilot.entity.CareerPlan;
import com.careerpilot.entity.ResumeMaterial;
import com.careerpilot.entity.UserProfile;

import java.util.List;

public class AiChatResponse {

    private String reply;
    private UserProfile profile;
    private CareerPlan careerPlan;
    private AbilityAnalysis abilityAnalysis;
    private ResumeMaterial resumeMaterial;
    private List<String> tasks;

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public UserProfile getProfile() {
        return profile;
    }

    public void setProfile(UserProfile profile) {
        this.profile = profile;
    }

    public CareerPlan getCareerPlan() {
        return careerPlan;
    }

    public void setCareerPlan(CareerPlan careerPlan) {
        this.careerPlan = careerPlan;
    }

    public AbilityAnalysis getAbilityAnalysis() {
        return abilityAnalysis;
    }

    public void setAbilityAnalysis(AbilityAnalysis abilityAnalysis) {
        this.abilityAnalysis = abilityAnalysis;
    }

    public ResumeMaterial getResumeMaterial() {
        return resumeMaterial;
    }

    public void setResumeMaterial(ResumeMaterial resumeMaterial) {
        this.resumeMaterial = resumeMaterial;
    }

    public List<String> getTasks() {
        return tasks;
    }

    public void setTasks(List<String> tasks) {
        this.tasks = tasks;
    }
}
