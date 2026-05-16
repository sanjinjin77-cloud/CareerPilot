<script setup lang="ts">
import { ref } from 'vue';
import { ElMessage } from 'element-plus';

import { generatePlanApi, planToBlocks } from '@/api/career';
import { mockProfile } from '@/mock/careerPilotMock';
import { getInitialCareerPlanBlocks, refreshCareerPlanMock } from '@/services/mockCareerService';
import type { CareerPlanBlock } from '@/types';

const planBlocks = ref<CareerPlanBlock[]>(getInitialCareerPlanBlocks());
const generatedAt = ref('刚刚生成');
const activeStep = ref(2);
const loading = ref(false);

async function handleRefreshPlan() {
  try {
    loading.value = true;
    const plan = await generatePlanApi({
      stage: mockProfile.currentStage,
      targetPosition: mockProfile.targetPosition,
    });

    planBlocks.value = planToBlocks(plan);
    generatedAt.value = '已从后端生成';
    activeStep.value = 3;
    ElMessage.success('已从后端生成职业规划');
  } catch {
    const result = refreshCareerPlanMock(mockProfile);

    planBlocks.value = result.blocks;
    generatedAt.value = result.generatedAt;
    activeStep.value = result.activeStep;
    ElMessage.warning('后端不可用，已使用 mock 职业规划');
  } finally {
    loading.value = false;
  }
}
</script>

<template>
  <section class="page-stack">
    <div class="module-hero">
      <div>
        <p class="eyebrow">Career Roadmap</p>
        <h1>从当前阶段到目标岗位的成长路线</h1>
        <p class="page-intro">
          根据年级、专业、目标岗位和能力基础，生成短期、中期和长期规划。
        </p>
      </div>
      <div class="module-hero__status">
        <span>当前关卡</span>
        <strong>{{ mockProfile.currentStage }}</strong>
        <em>{{ generatedAt }} · {{ mockProfile.targetPosition }}</em>
      </div>
    </div>

    <el-card shadow="never" class="module-card plan-timeline">
      <template #header>
        <div class="card-header-action">
          <span>阶段目标总览</span>
          <el-button type="primary" plain :loading="loading" @click="handleRefreshPlan">
            重新生成规划
          </el-button>
        </div>
      </template>
      <el-steps :active="activeStep" finish-status="success" align-center>
        <el-step title="职业认知" />
        <el-step title="技能学习" />
        <el-step title="项目积累" />
        <el-step title="简历优化" />
        <el-step title="模拟面试" />
      </el-steps>
    </el-card>

    <el-row :gutter="22">
      <el-col v-for="block in planBlocks" :key="block.title" :xs="24" :md="8">
        <el-card shadow="never" class="module-card stage-card">
          <template #header>{{ block.title }}</template>
          <ul class="clean-list">
            <li v-for="task in block.tasks" :key="task">{{ task }}</li>
          </ul>
        </el-card>
      </el-col>
    </el-row>
  </section>
</template>
