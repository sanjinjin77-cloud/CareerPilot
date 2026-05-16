<script setup lang="ts">
import { reactive, ref } from 'vue';
import { ElMessage } from 'element-plus';
import { useRouter } from 'vue-router';

import { useAuthStore } from '@/stores/auth';

const router = useRouter();
const authStore = useAuthStore();
const loading = ref(false);

const form = reactive({
  username: '',
  email: '',
  password: '',
});

async function handleRegister() {
  try {
    loading.value = true;
    await authStore.register(form);
    ElMessage.success('注册成功，请完善用户画像');
    router.push('/profile');
  } catch {
    ElMessage.error('注册失败，请检查邮箱是否已注册或后端服务是否启动');
  } finally {
    loading.value = false;
  }
}
</script>

<template>
  <main class="auth-page">
    <section class="auth-panel">
      <div class="auth-panel__intro">
        <p class="eyebrow">新用户注册</p>
        <h1>开启你的职业成长档案</h1>
        <p>
          注册后优先完成用户画像采集，系统将据此生成阶段化职业成长路线。
        </p>
      </div>

      <el-form label-position="top" class="auth-form" @submit.prevent="handleRegister">
        <el-form-item label="用户名">
          <el-input v-model="form.username" size="large" placeholder="例如：张同学" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="form.email" size="large" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input
            v-model="form.password"
            size="large"
            type="password"
            placeholder="至少 6 位"
            show-password
          />
        </el-form-item>
        <el-button
          type="primary"
          size="large"
          class="auth-form__submit"
          :loading="loading"
          @click="handleRegister"
        >
          注册并填写画像
        </el-button>
        <p class="auth-form__switch">
          已有账号？
          <router-link to="/login">去登录</router-link>
        </p>
      </el-form>
    </section>
  </main>
</template>
