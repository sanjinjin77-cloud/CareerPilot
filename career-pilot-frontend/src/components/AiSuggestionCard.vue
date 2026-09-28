<script setup lang="ts">
defineProps<{
  title: string;
  suggestion: string;
  path: string;
}>();
</script>

<template>
  <section class="ai-suggestion">
    <div class="ai-suggestion__icon">AI</div>
    <div>
      <p class="eyebrow">今日 AI 建议</p>
      <h2>{{ title }}</h2>
      <p>{{ suggestion }}</p>
    </div>
    <router-link class="cp-button cp-button--primary" :to="path">去完善素材</router-link>
  </section>
</template>
