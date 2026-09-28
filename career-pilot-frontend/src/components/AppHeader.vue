<script setup lang="ts">
import { computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';

import { useAuthStore } from '@/stores/auth';

const route = useRoute();
const router = useRouter();
const authStore = useAuthStore();

const navItems = [
  { label: '首页', path: '/' },
  { label: '个人画像', path: '/profile' },
  { label: '学习规划', path: '/learning-plan' },
  {
    label: '职业规划',
    path: '/career/job-match',
    children: [
      { label: '岗位匹配', path: '/career/job-match' },
      { label: '岗位筛选', path: '/career/job-filter' },
      { label: 'JD 分析', path: '/career/jd-analysis' },
    ],
  },
  { label: '能力分析', path: '/ability' },
  {
    label: '我的简历',
    path: '/resume/materials',
    children: [
      { label: '简历素材', path: '/resume/materials' },
      { label: '简历草稿', path: '/resume/templates' },
    ],
  },
  { label: '模拟面试', path: '/interview' },
];

const activePath = computed(() => route.path);

function isActive(item: (typeof navItems)[number]) {
  if (item.path === '/') {
    return activePath.value === '/';
  }

  return activePath.value === item.path
    || activePath.value.startsWith(`${item.path}/`)
    || Boolean(item.children?.some((child) => activePath.value === child.path));
}

function handleLogout() {
  authStore.logout();
  router.push('/login');
}
</script>

<template>
  <header class="app-header">
    <router-link class="app-header__brand" to="/">
      <span class="app-header__logo">CP</span>
      <span>
        <strong>CareerPilot</strong>
        <small>AI 职业成长陪跑平台</small>
      </span>
    </router-link>

    <nav class="app-header__nav">
      <div
        v-for="item in navItems"
        :key="item.path"
        class="app-header__nav-item"
        :class="{ active: isActive(item), 'has-dropdown': item.children }"
      >
        <router-link :to="item.path">
          {{ item.label }}
        </router-link>

        <div v-if="item.children" class="app-header__dropdown">
          <router-link
            v-for="child in item.children"
            :key="child.path"
            :to="child.path"
            :class="{ active: activePath === child.path }"
          >
            {{ child.label }}
          </router-link>
        </div>
      </div>
    </nav>

    <div class="app-header__user">
      <router-link class="app-header__profile" to="/profile">
        <span class="app-header__avatar">学</span>
        <span>成长档案</span>
      </router-link>
      <el-button plain type="success" @click="handleLogout">退出</el-button>
    </div>
  </header>
</template>
