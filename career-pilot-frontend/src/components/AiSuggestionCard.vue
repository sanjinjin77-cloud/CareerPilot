<script setup lang="ts">
import { aiSuggestion } from '@/mock/careerPilotMock';
</script>

<template>
  <section class="ai-suggestion">
    <div class="ai-suggestion__icon">AI</div>
    <div>
      <p class="eyebrow">今日 AI 建议</p>
      <h2>本周重点补强 TypeScript 和项目工程化表达</h2>
      <p>{{ aiSuggestion }}</p>
    </div>
    <router-link class="cp-button cp-button--primary" to="/resume">去完善素材</router-link>
  </section>
</template>
