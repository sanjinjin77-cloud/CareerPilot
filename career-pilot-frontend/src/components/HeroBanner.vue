<script setup lang="ts">
import { computed } from 'vue';

import type { DashboardSnapshot } from '@/types';

const props = defineProps<{
  dashboard: DashboardSnapshot;
}>();

const completedMilestones = computed(() => Math.max(0, Math.min(
  props.dashboard.milestoneTotal,
  props.dashboard.milestoneStep - 1,
)));

const progressWidth = computed(() => (
  `${Math.round((completedMilestones.value / Math.max(1, props.dashboard.milestoneTotal)) * 100)}%`
));

const trendLabel = computed(() => {
  const trend = props.dashboard.matchTrend;
  if (trend === 0) return '最新岗位评估';
  return `较上次 ${trend > 0 ? '+' : ''}${trend}%`;
});
</script>

<template>
  <section class="hero-shell">
    <div class="hero-bg-decoration">
      <span class="hero-orbit hero-orbit--one"></span>
      <span class="hero-orbit hero-orbit--two"></span>
      <span class="hero-flag">Offer</span>
    </div>

    <div class="hero-content">
      <div class="hero-left">
        <p class="eyebrow">AI Career Growth Agent</p>
        <h1>从入学到 Offer，CareerPilot 陪你一路通关</h1>
        <p class="hero-banner__subtitle">
          基于大语言模型、用户画像与岗位能力模型，为大学生提供职业规划、能力提升、简历优化、JD
          匹配和模拟面试的全周期成长陪跑服务。
        </p>
        <div class="hero-banner__actions">
          <router-link class="cp-button cp-button--primary" to="/profile">开始职业测评</router-link>
          <router-link class="cp-button cp-button--ghost" to="/planning">查看我的成长路线</router-link>
        </div>
      </div>

      <div class="hero-right" aria-label="AI 成长控制台">
        <div class="growth-console">
          <div class="growth-console__header">
            <span class="assistant-card__icon">AI</span>
            <div>
              <strong>AI 成长控制台</strong>
              <small>{{ dashboard.currentStage }} · {{ dashboard.targetPosition }}</small>
            </div>
          </div>

          <div class="console-route">
            <span
              v-for="(label, index) in dashboard.milestoneLabels"
              :key="label"
              :class="{
                done: index + 1 < dashboard.milestoneStep,
                current: index + 1 === dashboard.milestoneStep,
              }"
            >
              {{ label }}
            </span>
          </div>

          <div class="console-message">
            {{ dashboard.suggestion }}
          </div>
        </div>

        <div class="floating-card match-card">
          <small>岗位匹配度</small>
          <strong>{{ dashboard.matchScore }}%</strong>
          <span>{{ trendLabel }}</span>
        </div>

        <div class="floating-card progress-card">
          <small>通关进度</small>
          <strong>{{ completedMilestones }} / {{ dashboard.milestoneTotal }}</strong>
          <div class="mini-progress"><span :style="{ width: progressWidth }"></span></div>
        </div>

        <div class="offer-badge">
          <span>终点</span>
          <strong>Offer 冲刺</strong>
        </div>
      </div>
    </div>
  </section>
</template>
