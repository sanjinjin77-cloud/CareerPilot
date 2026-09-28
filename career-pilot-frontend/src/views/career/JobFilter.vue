<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue';
import { ElMessage } from 'element-plus';

import { getCurrentTargetOfferApi, getJobsApi, setTargetOfferApi } from '@/api/career';
import type { JobPosition, TargetOffer } from '@/types';

const jobs = ref<JobPosition[]>([]);
const currentTargetOffer = ref<TargetOffer | null>(null);
const loading = ref(false);
const settingJobId = ref<number | null>(null);

const filters = reactive({
  keyword: '',
  category: '',
  city: '',
  jobType: '',
  techStack: '',
});

const jobCountText = computed(() => `${jobs.value.length} 个`);
const currentTargetText = computed(() =>
  currentTargetOffer.value
    ? `${currentTargetOffer.value.companyName} · ${currentTargetOffer.value.jobTitle}`
    : '尚未设置',
);

const categoryOptions = computed(() => unique(jobs.value.map((job) => job.jobCategory)));
const cityOptions = computed(() => unique(jobs.value.map((job) => job.city)));
const jobTypeOptions = computed(() => unique(jobs.value.map((job) => job.jobType)));

function unique(values: string[]) {
  return [...new Set(values.map((value) => value || '').filter(Boolean))];
}

function salaryText(job: JobPosition) {
  if (!job.salaryMin && !job.salaryMax) return '薪资面议';
  return `${job.salaryMin}-${job.salaryMax}/天`;
}

function isCurrentTarget(job: JobPosition) {
  return currentTargetOffer.value?.jobId === job.id;
}

async function loadJobs() {
  try {
    loading.value = true;
    jobs.value = await getJobsApi(filters);
  } catch {
    ElMessage.error('岗位列表加载失败，请确认后端服务和登录状态');
  } finally {
    loading.value = false;
  }
}

async function loadTargetOffer() {
  try {
    currentTargetOffer.value = await getCurrentTargetOfferApi();
  } catch {
    currentTargetOffer.value = null;
  }
}

async function handleSetTarget(job: JobPosition) {
  try {
    settingJobId.value = job.id;
    currentTargetOffer.value = await setTargetOfferApi(job.id);
    ElMessage.success('已设为当前目标 Offer');
  } catch {
    ElMessage.error('设置目标 Offer 失败，请确认后端服务和登录状态');
  } finally {
    settingJobId.value = null;
  }
}

function resetFilters() {
  filters.keyword = '';
  filters.category = '';
  filters.city = '';
  filters.jobType = '';
  filters.techStack = '';
  loadJobs();
}

onMounted(async () => {
  await Promise.all([loadJobs(), loadTargetOffer()]);
});
</script>

<template>
  <main class="page-stack">
    <section class="module-hero">
      <div>
        <p class="eyebrow">Job Library</p>
        <h1>岗位筛选与目标 Offer</h1>
        <p class="page-intro">
          从本地岗位库筛选适合自己的目标岗位，设置后会作为能力分析、学习规划、简历定制和模拟面试的共同方向。
        </p>
      </div>
      <div class="module-hero__status">
        <span>当前目标</span>
        <strong>{{ currentTargetText }}</strong>
        <em>岗位库 {{ jobCountText }}</em>
      </div>
    </section>

    <el-card shadow="never" class="module-card filter-panel">
      <el-form :model="filters" label-position="top">
        <div class="filter-grid">
          <el-form-item label="关键词">
            <el-input v-model="filters.keyword" placeholder="岗位、公司或描述关键词" clearable />
          </el-form-item>
          <el-form-item label="岗位类别">
            <el-select v-model="filters.category" placeholder="全部类别" clearable>
              <el-option v-for="item in categoryOptions" :key="item" :label="item" :value="item" />
            </el-select>
          </el-form-item>
          <el-form-item label="城市">
            <el-select v-model="filters.city" placeholder="全部城市" clearable>
              <el-option v-for="item in cityOptions" :key="item" :label="item" :value="item" />
            </el-select>
          </el-form-item>
          <el-form-item label="工作类型">
            <el-select v-model="filters.jobType" placeholder="全部类型" clearable>
              <el-option v-for="item in jobTypeOptions" :key="item" :label="item" :value="item" />
            </el-select>
          </el-form-item>
          <el-form-item label="技术栈">
            <el-input v-model="filters.techStack" placeholder="Vue / Java / MySQL" clearable />
          </el-form-item>
        </div>
        <div class="filter-actions">
          <el-button type="primary" :loading="loading" @click="loadJobs">筛选岗位</el-button>
          <el-button @click="resetFilters">重置</el-button>
        </div>
      </el-form>
    </el-card>

    <el-card shadow="never" class="module-card" v-loading="loading">
      <template #header>
        <div class="section-header">
          <span>岗位结果</span>
          <small>{{ jobCountText }}</small>
        </div>
      </template>

      <el-empty v-if="!jobs.length && !loading" description="暂无匹配岗位" />

      <div v-else class="job-list">
        <article v-for="job in jobs" :key="job.id">
          <div class="job-card__main">
            <div class="job-card__title">
              <div>
                <h2>{{ job.jobTitle }}</h2>
                <span>{{ job.companyName }} · {{ job.city }}</span>
              </div>
              <el-tag :type="isCurrentTarget(job) ? 'success' : 'info'">
                {{ isCurrentTarget(job) ? '当前目标' : job.jobCategory }}
              </el-tag>
            </div>

            <div class="job-meta">
              <span>{{ salaryText(job) }}</span>
              <span>{{ job.educationRequirement }}</span>
              <span>{{ job.experienceRequirement }}</span>
              <span>{{ job.workMode }}</span>
            </div>

            <p>{{ job.jobDescription }}</p>

            <div class="tech-tags">
              <el-tag v-for="skill in job.techStack.split(',')" :key="`${job.id}-${skill}`" size="small">
                {{ skill.trim() }}
              </el-tag>
            </div>

            <div class="job-requirement">
              <strong>岗位要求</strong>
              <p>{{ job.jobRequirement }}</p>
            </div>
          </div>

          <div class="job-card__actions">
            <el-button
              type="primary"
              :plain="!isCurrentTarget(job)"
              :loading="settingJobId === job.id"
              @click="handleSetTarget(job)"
            >
              {{ isCurrentTarget(job) ? '已设为目标' : '设为目标 Offer' }}
            </el-button>
          </div>
        </article>
      </div>
    </el-card>
  </main>
</template>

<style scoped>
.filter-grid {
  display: grid;
  grid-template-columns: repeat(5, minmax(0, 1fr));
  gap: 14px;
}

.filter-actions,
.section-header,
.job-card__title,
.job-meta,
.tech-tags {
  display: flex;
  align-items: center;
  gap: 10px;
}

.filter-actions {
  justify-content: flex-end;
}

.section-header,
.job-card__title {
  justify-content: space-between;
}

.section-header small {
  color: #64748b;
}

.job-list {
  display: grid;
  gap: 14px;
}

.job-list article {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 150px;
  gap: 18px;
  align-items: center;
  padding: 18px;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
}

.job-card__main {
  display: grid;
  gap: 12px;
}

.job-card__title h2 {
  margin: 0 0 4px;
  font-size: 18px;
}

.job-card__title span,
.job-meta,
.job-requirement p {
  color: #64748b;
}

.job-meta,
.tech-tags {
  flex-wrap: wrap;
}

.job-card__main > p,
.job-requirement p {
  margin: 0;
  line-height: 1.65;
}

.job-requirement {
  display: grid;
  gap: 6px;
  padding: 12px;
  border-radius: 8px;
  background: #f8fafc;
}

.job-card__actions {
  display: flex;
  justify-content: flex-end;
}

@media (max-width: 960px) {
  .filter-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .job-list article {
    grid-template-columns: 1fr;
  }

  .job-card__actions {
    justify-content: flex-start;
  }
}

@media (max-width: 640px) {
  .filter-grid {
    grid-template-columns: 1fr;
  }

  .section-header,
  .job-card__title {
    align-items: flex-start;
    flex-direction: column;
  }
}
</style>
