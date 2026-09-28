<script setup lang="ts">
import { computed, onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';

import { getCurrentTargetOfferApi, getJobMatchesApi, setTargetOfferApi } from '@/api/career';
import type { JobMatch, JobPosition, TargetOffer } from '@/types';

const router = useRouter();

const matches = ref<JobMatch[]>([]);
const currentTargetOffer = ref<TargetOffer | null>(null);
const loading = ref(false);
const settingJobId = ref<number | null>(null);

const topMatch = computed(() => matches.value[0] || null);
const averageScore = computed(() => {
  if (!matches.value.length) return 0;
  const sum = matches.value.reduce((total, item) => total + item.matchScore, 0);
  return Math.round(sum / matches.value.length);
});
const targetText = computed(() =>
  currentTargetOffer.value
    ? `${currentTargetOffer.value.companyName} · ${currentTargetOffer.value.jobTitle}`
    : '尚未设置目标 Offer',
);

function salaryText(job: JobPosition) {
  if (!job.salaryMin && !job.salaryMax) return '薪资面议';
  return `${job.salaryMin}-${job.salaryMax}/月`;
}

function isCurrentTarget(job: JobPosition) {
  return currentTargetOffer.value?.jobId === job.id;
}

function reasonText(reason: string) {
  if (!reason || reason.includes('�') || reason.includes('锛') || reason.includes('宀')) {
    return '该岗位与当前画像存在一定匹配，可结合技能栈、城市和岗位要求进一步评估。';
  }
  return reason;
}

async function loadMatches() {
  try {
    loading.value = true;
    const [matchItems, targetOffer] = await Promise.all([
      getJobMatchesApi(),
      getCurrentTargetOfferApi().catch(() => null),
    ]);
    matches.value = matchItems;
    currentTargetOffer.value = targetOffer;
  } catch {
    ElMessage.error('岗位匹配加载失败，请确认后端服务和登录状态');
  } finally {
    loading.value = false;
  }
}

async function handleSetTarget(job: JobPosition) {
  try {
    settingJobId.value = job.id;
    currentTargetOffer.value = await setTargetOfferApi(job.id);
    ElMessage.success('已设为当前目标 Offer');
    return true;
  } catch {
    ElMessage.error('设置目标 Offer 失败，请确认后端服务和登录状态');
    return false;
  } finally {
    settingJobId.value = null;
  }
}

async function handleAnalyzeJob(job: JobPosition) {
  if (!isCurrentTarget(job)) {
    const ok = await handleSetTarget(job);
    if (!ok) return;
  }

  router.push('/career/jd-analysis');
}

onMounted(loadMatches);
</script>

<template>
  <main class="page-stack">
    <section class="module-hero">
      <div>
        <p class="eyebrow">Job Match</p>
        <h1>岗位匹配推荐</h1>
        <p class="page-intro">
          根据用户画像、目标城市、技能基础和岗位技术栈计算岗位匹配度，并把目标 Offer 回流到学习、简历和面试链路。
        </p>
      </div>
      <div class="module-hero__status">
        <span>当前目标</span>
        <strong>{{ targetText }}</strong>
        <em>平均匹配度 {{ averageScore }}%</em>
      </div>
    </section>

    <div v-if="topMatch" class="match-highlight">
      <div>
        <p class="eyebrow">Best Match</p>
        <h2>{{ topMatch.job.companyName }} · {{ topMatch.job.jobTitle }}</h2>
        <span>{{ topMatch.job.city }} · {{ salaryText(topMatch.job) }} · {{ topMatch.job.jobType }}</span>
      </div>
      <div class="match-highlight__score">
        <strong>{{ topMatch.matchScore }}%</strong>
        <el-button type="primary" :loading="settingJobId === topMatch.job.id" @click="handleSetTarget(topMatch.job)">
          {{ isCurrentTarget(topMatch.job) ? '当前目标' : '设为目标' }}
        </el-button>
        <el-button plain @click="handleAnalyzeJob(topMatch.job)">进入 JD 分析</el-button>
      </div>
    </div>

    <el-card shadow="never" class="module-card" v-loading="loading">
      <template #header>
        <div class="section-header">
          <span>推荐岗位</span>
          <div>
            <el-button size="small" @click="router.push('/career/job-filter')">筛选岗位</el-button>
            <el-button size="small" type="primary" plain @click="loadMatches">刷新匹配</el-button>
          </div>
        </div>
      </template>

      <el-empty v-if="!matches.length && !loading" description="暂无匹配结果">
        <el-button type="primary" @click="router.push('/profile')">完善个人画像</el-button>
      </el-empty>

      <div v-else class="match-list">
        <article v-for="item in matches" :key="item.job.id">
          <div class="match-score">
            <strong>{{ item.matchScore }}%</strong>
            <span>匹配度</span>
          </div>

          <div class="match-content">
            <div class="match-title">
              <div>
                <h2>{{ item.job.jobTitle }}</h2>
                <span>{{ item.job.companyName }} · {{ item.job.city }}</span>
              </div>
              <el-tag :type="isCurrentTarget(item.job) ? 'success' : 'info'">
                {{ isCurrentTarget(item.job) ? '当前目标' : item.job.jobCategory }}
              </el-tag>
            </div>

            <div class="job-meta">
              <span>{{ salaryText(item.job) }}</span>
              <span>{{ item.job.educationRequirement }}</span>
              <span>{{ item.job.experienceRequirement }}</span>
              <span>{{ item.job.workMode }}</span>
            </div>

            <div class="tech-tags">
              <el-tag v-for="skill in item.job.techStack.split(',')" :key="`${item.job.id}-${skill}`" size="small">
                {{ skill.trim() }}
              </el-tag>
            </div>

            <ul class="reason-list">
              <li v-for="reason in item.matchReasons" :key="`${item.job.id}-${reason}`">
                {{ reasonText(reason) }}
              </li>
            </ul>

            <p>{{ item.job.jobRequirement }}</p>
          </div>

          <div class="match-actions">
            <el-button
              type="primary"
              :plain="!isCurrentTarget(item.job)"
              :loading="settingJobId === item.job.id"
              @click="handleSetTarget(item.job)"
            >
              {{ isCurrentTarget(item.job) ? '已设为目标' : '设为目标 Offer' }}
            </el-button>
            <el-button plain @click="handleAnalyzeJob(item.job)">JD 分析</el-button>
          </div>
        </article>
      </div>
    </el-card>
  </main>
</template>

<style scoped>
.match-highlight {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 18px;
  padding: 22px;
  border-radius: 8px;
  background: #0f172a;
  color: #fff;
}

.match-highlight h2 {
  margin: 4px 0 8px;
  font-size: 22px;
}

.match-highlight span {
  color: #cbd5e1;
}

.match-highlight__score,
.section-header,
.section-header > div,
.match-title,
.job-meta,
.tech-tags,
.match-actions {
  display: flex;
  align-items: center;
  gap: 10px;
}

.match-highlight__score {
  flex-wrap: wrap;
  justify-content: flex-end;
}

.match-highlight__score strong {
  font-size: 34px;
}

.section-header,
.match-title {
  justify-content: space-between;
}

.match-list {
  display: grid;
  gap: 14px;
}

.match-list article {
  display: grid;
  grid-template-columns: 92px minmax(0, 1fr) 170px;
  gap: 18px;
  align-items: start;
  padding: 18px;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
}

.match-score {
  display: grid;
  justify-items: center;
  gap: 4px;
  padding: 12px;
  border-radius: 8px;
  background: #eff6ff;
  color: #2563eb;
}

.match-score strong {
  font-size: 24px;
}

.match-score span,
.match-title span,
.job-meta {
  color: #64748b;
}

.match-content {
  display: grid;
  gap: 12px;
}

.match-title h2 {
  margin: 0 0 4px;
  font-size: 18px;
}

.job-meta,
.tech-tags {
  flex-wrap: wrap;
}

.reason-list {
  display: grid;
  gap: 6px;
  margin: 0;
  padding-left: 18px;
  color: #334155;
}

.match-content > p {
  margin: 0;
  line-height: 1.65;
  color: #475569;
}

.match-actions {
  align-items: stretch;
  flex-direction: column;
  justify-content: flex-start;
}

@media (max-width: 960px) {
  .match-highlight,
  .section-header,
  .match-title {
    align-items: flex-start;
    flex-direction: column;
  }

  .match-list article {
    grid-template-columns: 1fr;
  }

  .match-score {
    justify-items: start;
  }

  .match-actions {
    flex-direction: row;
    flex-wrap: wrap;
  }
}
</style>
