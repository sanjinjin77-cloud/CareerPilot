<script setup lang="ts">
const props = defineProps<{
  activeStep: number;
  labels: string[];
}>();

const descriptions = [
  '明确专业方向和可选岗位。',
  '建立岗位所需能力基础。',
  '把课程和训练转化为作品。',
  '沉淀可投递的简历表达。',
  '围绕项目和岗位持续训练。',
  '复盘投递并提升转化率。',
];

const levelLabels = ['第一关', '第二关', '第三关', '第四关', '第五关', '第六关'];
</script>

<template>
  <section class="home-section">
    <div class="section-heading">
      <p class="eyebrow">Growth Roadmap</p>
      <h2>通关式职业成长路径</h2>
      <span>把大学四年拆解为可执行的职业成长关卡。</span>
    </div>

    <div class="roadmap-map">
      <article
        v-for="(label, index) in props.labels"
        :key="label"
        class="roadmap-node"
        :class="{
          'roadmap-node--done': index + 1 < activeStep,
          'roadmap-node--current': index + 1 === activeStep,
        }"
      >
        <span class="roadmap-node__level">{{ levelLabels[index] || `第 ${index + 1} 关` }}</span>
        <i>{{ String(index + 1).padStart(2, '0') }}</i>
        <strong>{{ label }}</strong>
        <p>{{ descriptions[index] || '继续推进职业成长闭环。' }}</p>
      </article>
    </div>
  </section>
</template>
