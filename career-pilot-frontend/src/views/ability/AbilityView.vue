<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { ElMessage } from 'element-plus';

import { analyzeAbilityApi } from '@/api/career';
import { abilityReport as mockAbilityReport } from '@/mock/careerPilotMock';
import type { AbilityReport } from '@/types';

const abilityReport = ref<AbilityReport>({ ...mockAbilityReport });

async function refreshAbilityReport() {
  try {
    abilityReport.value = await analyzeAbilityApi(abilityReport.value.targetPosition);
    ElMessage.success('已从后端生成能力分析');
  } catch {
    ElMessage.warning('后端不可用，继续使用 mock 能力分析');
  }
}

onMounted(refreshAbilityReport);
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
        <em>{{ abilityReport.suggestions[0] }}</em>
      </div>
    </div>

    <el-card shadow="never" class="module-card">
      <el-row :gutter="20" align="middle">
        <el-col :xs="24" :md="8" class="ability-score">
          <el-progress type="dashboard" :percentage="abilityReport.matchScore" />
          <h3>{{ abilityReport.targetPosition }}匹配度</h3>
          <p>已具备页面开发基础，下一阶段重点提升工程化和项目表达。</p>
        </el-col>
        <el-col :xs="24" :md="16">
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
