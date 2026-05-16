<template>
  <section class="home-page">
    <HeroBanner />
    <SummaryCards />
    <GrowthRoadmap />
    <FeatureGrid />
    <AiSuggestionCard />
  </section>
</template>

<script setup lang="ts">
import AiSuggestionCard from '@/components/AiSuggestionCard.vue';
import FeatureGrid from '@/components/FeatureGrid.vue';
import GrowthRoadmap from '@/components/GrowthRoadmap.vue';
import HeroBanner from '@/components/HeroBanner.vue';
import SummaryCards from '@/components/SummaryCards.vue';
</script>
