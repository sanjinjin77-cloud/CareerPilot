<script setup lang="ts">
import { computed, onBeforeUnmount, onMounted, ref, watch } from 'vue';
import { ElMessage } from 'element-plus';
import { RadarChart } from 'echarts/charts';
import { RadarComponent } from 'echarts/components';
import { init, use, type EChartsType } from 'echarts/core';
import { CanvasRenderer } from 'echarts/renderers';

import { analyzeAbilityApi } from '@/api/career';
import { getProfileApi } from '@/api/profile';
import { abilityReport as mockAbilityReport } from '@/mock/careerPilotMock';
import type { AbilityReport } from '@/types';

const abilityReport = ref<AbilityReport>({ ...mockAbilityReport });
const chartElement = ref<HTMLDivElement | null>(null);
const usingFallback = ref(true);
let chart: EChartsType | null = null;

use([RadarChart, RadarComponent, CanvasRenderer]);

const sourceLabel = computed(() => (
  abilityReport.value.analysisSource === 'llm' ? '大模型分析' : '本地降级'
));

function renderRadar() {
  if (!chartElement.value) return;
  if (!chart) chart = init(chartElement.value);

  const scores = Object.entries(abilityReport.value.skillScores);
  chart.setOption({
    animationDuration: 500,
    radar: {
      radius: '64%',
      indicator: scores.map(([name]) => ({ name, max: 100 })),
      splitNumber: 4,
      axisName: { color: '#374151' },
      splitArea: { areaStyle: { color: ['#ffffff', '#f8fafc'] } },
      splitLine: { lineStyle: { color: '#dbe3ec' } },
      axisLine: { lineStyle: { color: '#cbd5e1' } },
    },
    series: [{
      type: 'radar',
      data: [{
        value: scores.map(([, score]) => score),
        name: abilityReport.value.targetPosition,
        areaStyle: { color: 'rgba(37, 99, 235, 0.18)' },
        lineStyle: { color: '#2563eb', width: 2 },
        itemStyle: { color: '#0f766e' },
      }],
    }],
  });
}

async function refreshAbilityReport() {
  try {
    const profile = await getProfileApi();
    abilityReport.value = await analyzeAbilityApi(profile.targetPosition || abilityReport.value.targetPosition);
    usingFallback.value = false;
    ElMessage.success('已从后端生成能力分析');
  } catch {
    usingFallback.value = true;
    ElMessage.warning('能力分析加载失败，当前保留页面演示数据');
  }
}

watch(abilityReport, renderRadar, { deep: true, flush: 'post' });

onMounted(() => {
  renderRadar();
  window.addEventListener('resize', handleResize);
  refreshAbilityReport();
});

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize);
  chart?.dispose();
});

function handleResize() {
  chart?.resize();
}
</script>

<template>
  <section class="page-stack">
    <div class="module-hero">
      <div>
        <p class="eyebrow">Ability Gap</p>
        <h1>目标岗位匹配度与短板拆解</h1>
        <p class="page-intro">
          围绕目标岗位要求识别优势、短板和优先提升路径。
        </p>
      </div>
      <div class="module-hero__status">
        <span>目标岗位</span>
        <strong>{{ abilityReport.targetPosition }}</strong>
        <em>{{ abilityReport.summary }}</em>
      </div>
    </div>

    <el-card shadow="never" class="module-card">
      <template #header>
        <div class="card-header-action">
          <span>能力评估结果</span>
          <div class="source-meta">
            <el-tag v-if="usingFallback" type="warning">演示数据</el-tag>
            <el-tag :type="abilityReport.analysisSource === 'llm' ? 'success' : 'warning'">
              {{ sourceLabel }}
            </el-tag>
            <small v-if="abilityReport.model">{{ abilityReport.model }}</small>
          </div>
        </div>
      </template>
      <el-row :gutter="20" align="middle">
        <el-col :xs="24" :md="6" class="ability-score">
          <el-progress type="dashboard" :percentage="abilityReport.matchScore" />
          <h3>{{ abilityReport.targetPosition }}匹配度</h3>
          <p>{{ abilityReport.summary }}</p>
        </el-col>
        <el-col :xs="24" :md="8">
          <div ref="chartElement" class="ability-radar" aria-label="岗位能力雷达图"></div>
        </el-col>
        <el-col :xs="24" :md="10">
          <el-descriptions :column="1" border>
            <el-descriptions-item label="优势">
              {{ abilityReport.strengths.join('、') }}
            </el-descriptions-item>
            <el-descriptions-item label="短板">
              {{ abilityReport.weaknesses.join('、') }}
            </el-descriptions-item>
            <el-descriptions-item label="优先提升">
              {{ abilityReport.suggestions[0] }}
            </el-descriptions-item>
          </el-descriptions>
        </el-col>
      </el-row>
    </el-card>

    <div class="module-grid module-grid--three">
      <article class="insight-card">
        <span>优势</span>
        <strong>{{ abilityReport.strengths[0] }}清晰</strong>
        <p>可以承担基础页面开发和组件拆分任务。</p>
      </article>
      <article class="insight-card insight-card--active">
        <span>当前短板</span>
        <strong>{{ abilityReport.weaknesses[0] }} + {{ abilityReport.weaknesses[1] }}</strong>
        <p>建议结合现有项目做一次工程化重构。</p>
      </article>
      <article class="insight-card">
        <span>下一步</span>
        <strong>模拟面试训练</strong>
        <p>围绕项目亮点准备结构化表达。</p>
      </article>
    </div>
  </section>
</template>

<style scoped>
.ability-radar {
  width: 100%;
  height: 300px;
}

.source-meta {
  display: flex;
  align-items: center;
  gap: 10px;
}

.source-meta small {
  color: #6b7280;
}
</style>
