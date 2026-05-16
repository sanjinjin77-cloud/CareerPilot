<script setup lang="ts">
import { featureEntries } from '@/mock/careerPilotMock';
</script>

<template>
  <section class="home-section">
    <div class="section-heading">
      <p class="eyebrow">Core Features</p>
      <h2>从探索到投递的完整陪跑能力</h2>
      <span>每个模块都服务于职业成长闭环，而不是孤立的工具。</span>
    </div>

    <div class="feature-grid">
      <article v-for="feature in featureEntries" :key="feature.title" class="feature-card">
        <span class="feature-card__icon">{{ feature.icon }}</span>
        <h3>{{ feature.title }}</h3>
        <p>{{ feature.description }}</p>
        <router-link :to="feature.path">进入模块</router-link>
      </article>
    </div>
  </section>
</template>
