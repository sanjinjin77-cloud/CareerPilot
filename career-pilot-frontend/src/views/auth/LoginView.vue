<script setup lang="ts">
import { reactive, ref } from 'vue';
import { ElMessage } from 'element-plus';
import { useRoute, useRouter } from 'vue-router';

import { useAuthStore } from '@/stores/auth';

const router = useRouter();
const route = useRoute();
const authStore = useAuthStore();
const loading = ref(false);

const form = reactive({
  account: 'student@example.com',
  password: '123456',
});

async function handleLogin() {
  try {
    loading.value = true;
    await authStore.login(form);
    ElMessage.success('登录成功');
    router.push((route.query.redirect as string) || '/');
  } catch {
    ElMessage.error('登录失败，请检查账号或后端服务');
  } finally {
    loading.value = false;
  }
}
</script>

<template>
  <main class="auth-page">
    <section class="auth-panel">
      <div class="auth-panel__intro">
        <p class="eyebrow">CareerPilot</p>
        <h1>登录职业成长陪跑平台</h1>
        <p>
          登录后进入成长仪表盘，继续完善用户画像、查看职业规划，并沉淀求职素材。
        </p>
      </div>

      <el-form label-position="top" class="auth-form" @submit.prevent="handleLogin">
        <el-form-item label="账号 / 邮箱">
          <el-input v-model="form.account" size="large" placeholder="请输入账号或邮箱" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input
            v-model="form.password"
            size="large"
            type="password"
            placeholder="请输入密码"
            show-password
          />
        </el-form-item>
        <el-button
          type="primary"
          size="large"
          class="auth-form__submit"
          :loading="loading"
          @click="handleLogin"
        >
          登录
        </el-button>
        <p class="auth-form__switch">
          还没有账号？
          <router-link to="/register">去注册</router-link>
        </p>
      </el-form>
    </section>
  </main>
</template>
