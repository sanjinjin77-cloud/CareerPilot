<script setup lang="ts">
import { computed, reactive, ref } from 'vue';
import { ElMessage } from 'element-plus';

import { jdAnalysis } from '@/mock/careerPilotMock';
import { analyzeJDMock, appendJDOptimizationSuggestionMock } from '@/services/mockCareerService';

const jdContent = ref(jdAnalysis.jdContent);
const analysis = reactive({
  keywords: [...jdAnalysis.keywords],
  recommendedMaterials: [...jdAnalysis.recommendedMaterials],
  suggestions: [...jdAnalysis.suggestions],
  matchScore: jdAnalysis.matchScore,
});
const hasAnalyzed = ref(false);
const scoreText = computed(() => `${analysis.matchScore}%`);

function handleAnalyzeJD() {
  if (!jdContent.value.trim()) {
    ElMessage.warning('请先粘贴岗位 JD');
    return;
  }

  const result = analyzeJDMock(jdContent.value);

  analysis.keywords = result.keywords;
  analysis.matchScore = result.matchScore;
  analysis.recommendedMaterials = result.recommendedMaterials;
  analysis.suggestions = result.suggestions;
  hasAnalyzed.value = true;
  ElMessage.success('已完成 mock JD 分析');
}

function handleGenerateSuggestion() {
  if (!hasAnalyzed.value) {
    handleAnalyzeJD();
    return;
  }

  analysis.suggestions = appendJDOptimizationSuggestionMock(analysis.suggestions);
  ElMessage.success('已生成 mock 简历优化建议');
}
</script>

<template>
  <section class="page-stack">
    <div class="module-hero">
      <div>
        <p class="eyebrow">JD Match</p>
        <h1>用岗位要求反推简历表达</h1>
        <p class="page-intro">
          粘贴岗位 JD 后，系统将提取关键词、分析能力要求，并推荐可放入简历的素材。
        </p>
      </div>
      <div class="module-hero__status">
        <span>最近匹配度</span>
        <strong>{{ scoreText }}</strong>
        <em>{{ analysis.suggestions[0] }}</em>
      </div>
    </div>

    <div class="module-grid module-grid--side">
      <el-card shadow="never" class="module-card jd-input-card">
        <template #header>岗位 JD</template>
        <el-input v-model="jdContent" type="textarea" :rows="10" />
        <div class="action-row">
          <el-button type="primary" @click="handleAnalyzeJD">分析 JD</el-button>
          <el-button @click="handleGenerateSuggestion">生成优化建议</el-button>
        </div>
      </el-card>

      <el-card shadow="never" class="module-card">
        <template #header>分析预览</template>
        <div class="keyword-cloud">
          <el-tag v-for="keyword in analysis.keywords" :key="keyword">{{ keyword }}</el-tag>
        </div>
        <div class="match-preview">
          <span>推荐放入简历</span>
          <strong>{{ analysis.recommendedMaterials.join('、') }}</strong>
        </div>
        <ul class="clean-list jd-suggestions">
          <li v-for="suggestion in analysis.suggestions" :key="suggestion">{{ suggestion }}</li>
        </ul>
      </el-card>
    </div>
  </section>
</template>
