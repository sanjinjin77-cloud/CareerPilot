<template>
  <div class="app-shell">
    <AppHeader />
    <main class="app-shell__main">
      <router-view />
    </main>
    <GlobalAiAssistant />
  </div>
</template>

<script setup lang="ts">
import AppHeader from '@/components/AppHeader.vue';
import GlobalAiAssistant from '@/components/GlobalAiAssistant.vue';
</script>
