import { defineStore } from 'pinia';

import type {
  AiChatResponse,
  BackendAbilityAnalysis,
  BackendCareerPlan,
  BackendResumeMaterial,
  BackendUserProfile,
} from '@/api/ai';

interface DashboardSnapshot {
  currentStage: string;
  targetRole: string;
  matchScore: number;
  tasks: string[];
}

export const useCareerStore = defineStore('career', {
  state: () => ({
    profile: null as BackendUserProfile | null,
    careerPlan: null as BackendCareerPlan | null,
    abilityAnalysis: null as BackendAbilityAnalysis | null,
    latestResumeMaterial: null as BackendResumeMaterial | null,
    dashboard: {
      currentStage: '',
      targetRole: '',
      matchScore: 0,
      tasks: [],
    } as DashboardSnapshot,
  }),
  actions: {
    applyAiChatResponse(response: AiChatResponse) {
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
