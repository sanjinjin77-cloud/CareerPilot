import { defineStore } from 'pinia';
export const useCareerStore = defineStore('career', {
    state: () => ({
        profile: null,
        careerPlan: null,
        abilityAnalysis: null,
        latestResumeMaterial: null,
        dashboard: {
            currentStage: '',
            targetRole: '',
            matchScore: 0,
            tasks: [],
        },
    }),
    actions: {
        applyAiChatResponse(response) {
            if (response.profile) {
                this.profile = response.profile;
            }
            if (response.careerPlan) {
                this.careerPlan = response.careerPlan;
            }
            if (response.abilityAnalysis) {
                this.abilityAnalysis = response.abilityAnalysis;
            }
            if (response.resumeMaterial) {
                this.latestResumeMaterial = response.resumeMaterial;
            }
            this.dashboard = {
                currentStage: response.profile?.currentStage || response.careerPlan?.currentStage || this.dashboard.currentStage,
                targetRole: response.profile?.targetRole || response.abilityAnalysis?.targetRole || this.dashboard.targetRole,
                matchScore: response.abilityAnalysis?.matchScore ?? this.dashboard.matchScore,
                tasks: response.tasks || this.dashboard.tasks,
            };
        },
    },
});
