<template>
  <section class="home-page">
    <el-alert
      v-if="usingFallback"
      title="仪表盘接口暂不可用，当前显示演示数据。请恢复后端或登录状态后刷新。"
      type="warning"
      show-icon
      :closable="false"
    />
    <HeroBanner :dashboard="dashboard" />
    <SummaryCards :items="summaryItems" />
    <GrowthRoadmap
      :active-step="dashboard.milestoneStep"
      :labels="dashboard.milestoneLabels"
    />
    <FeatureGrid />
    <AiSuggestionCard
      :title="dashboard.suggestionTitle"
      :suggestion="dashboard.suggestion"
      :path="dashboard.suggestionPath"
    />
  </section>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue';
import { ElMessage } from 'element-plus';

import { getDashboardSummaryApi } from '@/api/dashboard';
import AiSuggestionCard from '@/components/AiSuggestionCard.vue';
import FeatureGrid from '@/components/FeatureGrid.vue';
import GrowthRoadmap from '@/components/GrowthRoadmap.vue';
import HeroBanner from '@/components/HeroBanner.vue';
import SummaryCards from '@/components/SummaryCards.vue';
import { dashboardSummary, mockDashboardSnapshot } from '@/mock/careerPilotMock';
import type { DashboardSnapshot, DashboardSummaryItem } from '@/types';

const dashboard = ref<DashboardSnapshot>({ ...mockDashboardSnapshot });
const usingFallback = ref(false);

const summaryItems = computed<DashboardSummaryItem[]>(() => [
  {
    ...dashboardSummary[0],
    value: `${dashboard.value.grade} · ${dashboard.value.currentStage}`,
    description: dashboard.value.currentLevel || dashboardSummary[0].description,
    progress: dashboard.value.learningProgress,
  },
  {
    ...dashboardSummary[1],
    value: dashboard.value.targetPosition || dashboardSummary[1].value,
    description: dashboard.value.targetOfferTitle
      ? `目标 Offer：${dashboard.value.targetOfferCompany} · ${dashboard.value.targetOfferTitle}`
      : dashboardSummary[1].description,
    progress: dashboard.value.targetOfferMatchScore || dashboard.value.matchScore,
  },
  {
    ...dashboardSummary[2],
    value: `${dashboard.value.matchScore}%`,
    description: dashboard.value.matchTrend
      ? `较上次评估 ${dashboard.value.matchTrend > 0 ? '+' : ''}${dashboard.value.matchTrend}%`
      : dashboardSummary[2].description,
    progress: dashboard.value.matchScore,
  },
  {
    ...dashboardSummary[3],
    value: `${dashboard.value.pendingTasks} 项`,
    description: dashboard.value.nextTaskTitle || dashboardSummary[3].description,
    progress: dashboard.value.totalTasks > 0
      ? Math.round((dashboard.value.completedTasks / dashboard.value.totalTasks) * 100)
      : dashboard.value.learningProgress,
  },
]);

async function loadDashboard() {
  try {
    dashboard.value = await getDashboardSummaryApi();
    usingFallback.value = false;
  } catch {
    dashboard.value = { ...mockDashboardSnapshot };
    usingFallback.value = true;
    ElMessage.warning('仪表盘加载失败，已切换为演示数据');
  }
}

onMounted(loadDashboard);
</script>
