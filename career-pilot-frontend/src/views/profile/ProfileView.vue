<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue';
import { ElMessage } from 'element-plus';

import { getProfileApi, saveProfileApi } from '@/api/profile';
import { mockProfile } from '@/mock/careerPilotMock';
import {
  calculateProfileCompletion,
  generatePlanPreviewMock,
} from '@/services/mockCareerService';

const profile = reactive({ ...mockProfile });
const generatedPlan = ref('');
const loading = ref(false);

const completion = computed(() => calculateProfileCompletion(profile));

async function handleSaveProfile() {
  try {
    loading.value = true;
    const savedProfile = await saveProfileApi(profile);
    Object.assign(profile, savedProfile);
    ElMessage.success('用户画像已保存到后端');
  } catch {
    ElMessage.warning('用户画像保存失败，当前修改仅保留在页面中，尚未写入数据库');
  } finally {
    loading.value = false;
  }
}

function handleGeneratePlan() {
  generatedPlan.value = generatePlanPreviewMock(profile);
  ElMessage.success('已根据当前画像生成本地规划预览');
}

onMounted(async () => {
  try {
    const savedProfile = await getProfileApi();
    Object.assign(profile, savedProfile);
  } catch {
    ElMessage.warning('画像加载失败，当前显示演示数据');
  }
});
</script>

<template>
  <section class="page-stack">
    <div class="module-hero">
      <div>
        <p class="eyebrow">Profile Builder</p>
        <h1>采集职业成长基础信息</h1>
        <p class="page-intro">
          用户画像是职业规划、能力分析、JD 匹配和模拟面试的共同基础。
        </p>
      </div>
      <div class="module-hero__status">
        <span>档案完整度</span>
        <strong>{{ completion }}%</strong>
        <div class="mini-progress"><span :style="{ width: `${completion}%` }"></span></div>
      </div>
    </div>

    <div class="module-grid module-grid--side">
      <el-card shadow="never" class="module-card">
        <template #header>成长档案摘要</template>
        <div class="profile-summary">
          <span>年级</span><strong>{{ profile.grade }}</strong>
          <span>专业</span><strong>{{ profile.major }}</strong>
          <span>目标岗位</span><strong>{{ profile.targetPosition }}</strong>
          <span>意向城市</span><strong>{{ profile.targetCity }}</strong>
          <span>当前阶段</span><strong>{{ profile.currentStage }}</strong>
          <span>档案完整度</span><strong>{{ completion }}%</strong>
        </div>
      </el-card>

      <el-card shadow="never" class="module-card">
        <template #header>画像采集表</template>
      <el-form :model="profile" label-width="110px">
        <el-row :gutter="18">
          <el-col :xs="24" :md="12">
            <el-form-item label="年级">
              <el-input v-model="profile.grade" />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :md="12">
            <el-form-item label="学历">
              <el-input v-model="profile.educationLevel" />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :md="12">
            <el-form-item label="学校类型">
              <el-input v-model="profile.schoolType" />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :md="12">
            <el-form-item label="专业">
              <el-input v-model="profile.major" />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :md="12">
            <el-form-item label="目标岗位">
              <el-input v-model="profile.targetPosition" />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :md="12">
            <el-form-item label="意向城市">
              <el-input v-model="profile.targetCity" />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :md="12">
            <el-form-item label="意向行业">
              <el-input v-model="profile.targetIndustry" />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :md="12">
            <el-form-item label="编程语言">
              <el-input v-model="profile.languages" />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :md="12">
            <el-form-item label="技术框架">
              <el-input v-model="profile.frameworks" />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :md="12">
            <el-form-item label="项目经历">
              <el-input v-model="profile.projects" />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :md="12">
            <el-form-item label="比赛经历">
              <el-input v-model="profile.competitions" />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :md="12">
            <el-form-item label="实习经历">
              <el-input v-model="profile.internships" />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :md="12">
            <el-form-item label="英语水平">
              <el-input v-model="profile.englishLevel" />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :md="12">
            <el-form-item label="证书情况">
              <el-input v-model="profile.certificates" />
            </el-form-item>
          </el-col>
          <el-col :xs="24">
            <el-form-item label="个人偏好">
              <el-checkbox-group v-model="profile.preferences">
                <el-checkbox label="技术岗" />
                <el-checkbox label="非技术岗" />
                <el-checkbox label="稳定" />
                <el-checkbox label="高成长" />
                <el-checkbox label="接受加班" />
                <el-checkbox label="接受异地" />
              </el-checkbox-group>
            </el-form-item>
          </el-col>
        </el-row>
        <div v-if="generatedPlan" class="mock-result">
          <span>本地规划预览</span>
          <p>{{ generatedPlan }}</p>
        </div>
        <el-button type="primary" :loading="loading" @click="handleSaveProfile">保存画像</el-button>
        <el-button @click="handleGeneratePlan">重新生成规划</el-button>
      </el-form>
      </el-card>
    </div>
  </section>
</template>
