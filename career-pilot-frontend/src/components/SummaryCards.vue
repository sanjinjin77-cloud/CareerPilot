<script setup lang="ts">
import { dashboardSummary } from '@/mock/careerPilotMock';
</script>

<template>
  <section class="summary-grid">
    <article v-for="item in dashboardSummary" :key="item.label" class="summary-card">
      <div class="summary-card__top">
        <span class="summary-card__icon">{{ item.icon }}</span>
        <em>{{ item.tag }}</em>
      </div>
      <small>{{ item.label }}</small>
      <strong>{{ item.value }}</strong>
      <p>{{ item.description }}</p>
      <div class="summary-card__bar">
        <span :style="{ width: `${item.progress}%` }"></span>
      </div>
    </article>
  </section>
</template>
