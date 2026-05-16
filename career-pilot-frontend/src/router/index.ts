import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router';

import { useAuthStore } from '@/stores/auth';

const routes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'login',
    component: () => import('@/views/auth/LoginView.vue'),
    meta: { title: '登录', public: true },
  },
  {
    path: '/register',
    name: 'register',
    component: () => import('@/views/auth/RegisterView.vue'),
    meta: { title: '注册', public: true },
  },
  {
    path: '/',
    component: () => import('@/layouts/MainLayout.vue'),
    meta: { requiresAuth: true },
    children: [
      {
        path: '',
        name: 'home',
        component: () => import('@/views/Home.vue'),
        meta: { title: '首页' },
      },
      {
        path: 'profile',
        name: 'profile',
        component: () => import('@/views/Profile.vue'),
        meta: { title: '个人画像' },
      },
      {
        path: 'learning-plan',
        name: 'learning-plan',
        component: () => import('@/views/LearningPlan.vue'),
        meta: { title: '学习规划' },
      },
      {
        path: 'career/job-match',
        name: 'job-match',
        component: () => import('@/views/career/JobMatch.vue'),
        meta: { title: '岗位匹配' },
      },
      {
        path: 'career/job-filter',
        name: 'job-filter',
        component: () => import('@/views/career/JobFilter.vue'),
        meta: { title: '岗位筛选' },
      },
      {
        path: 'ability',
        name: 'ability',
        component: () => import('@/views/Ability.vue'),
        meta: { title: '能力分析' },
      },
      {
        path: 'resume/materials',
        name: 'resume-materials',
        component: () => import('@/views/resume/ResumeMaterials.vue'),
        meta: { title: '简历素材' },
      },
      {
        path: 'resume/templates',
        name: 'resume-templates',
        component: () => import('@/views/resume/ResumeTemplates.vue'),
        meta: { title: '简历模板' },
      },
      {
        path: 'interview',
        name: 'interview',
        component: () => import('@/views/Interview.vue'),
        meta: { title: '模拟面试' },
      },
      {
        path: 'planning',
        redirect: '/learning-plan',
      },
      {
        path: 'resume',
        redirect: '/resume/materials',
      },
      {
        path: 'jd',
        redirect: '/career/job-match',
      },
    ],
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/',
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

router.beforeEach((to) => {
  const authStore = useAuthStore();

  if (to.meta.requiresAuth && !authStore.isAuthenticated) {
    return { name: 'login', query: { redirect: to.fullPath } };
  }

  if ((to.name === 'login' || to.name === 'register') && authStore.isAuthenticated) {
    return { name: 'home' };
  }

  return true;
});

export default router;
