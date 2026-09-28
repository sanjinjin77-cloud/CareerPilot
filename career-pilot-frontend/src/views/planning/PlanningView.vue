<script setup lang="ts">
import { computed, onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';

import {
  completeLearningTaskApi,
  createInterviewImprovementPlanApi,
  createMaterialFromLearningTaskApi,
  generateLearningPlanApi,
  generatePlanApi,
  getCurrentLearningPlanApi,
  getLearningTasksApi,
  getResumeMaterialsApi,
  planToBlocks,
} from '@/api/career';
import { getProfileApi } from '@/api/profile';
import { mockProfile } from '@/mock/careerPilotMock';
import type { CareerPlanBlock, LearningPlan, LearningTask, ResumeMaterial, UserProfile } from '@/types';

const router = useRouter();

const planBlocks = ref<CareerPlanBlock[]>([]);
const currentProfile = ref<UserProfile>({ ...mockProfile });
const learningPlan = ref<LearningPlan | null>(null);
const learningTasks = ref<LearningTask[]>([]);
const resumeMaterials = ref<ResumeMaterial[]>([]);
const generatedAt = ref('读取中');
const careerPlanSource = ref<'llm' | 'local_fallback'>('local_fallback');
const careerPlanModel = ref('');
const currentLevel = ref('');
const roadmapStages = ref<string[]>([]);
const activeStep = ref(2);
const loading = ref(false);
const taskLoadingId = ref<number | null>(null);
const materializingTaskId = ref<number | null>(null);
const generatingReviewTasks = ref(false);
const activeTaskFilter = ref<'all' | 'todo' | 'done' | 'interview'>('all');

const materializedTaskIds = computed(
  () => new Set(resumeMaterials.value.map((item) => item.sourceTaskId).filter(Boolean)),
);
const taskStats = computed(() => {
  const total = learningTasks.value.length;
  const done = learningTasks.value.filter((task) => task.status === 'done').length;
  const materialized = learningTasks.value.filter((task) => materializedTaskIds.value.has(task.id)).length;
  return { total, done, materialized };
});
const progressPercent = computed(() => {
  if (!learningTasks.value.length) return learningPlan.value?.progress || 0;
  return Math.round((taskStats.value.done / learningTasks.value.length) * 100);
});
const filteredTasks = computed(() => {
  if (activeTaskFilter.value === 'all') return learningTasks.value;
  if (activeTaskFilter.value === 'interview') {
    return learningTasks.value.filter((task) => task.taskType === 'interview');
  }
  return learningTasks.value.filter((task) => task.status === activeTaskFilter.value);
});
const filterItems = computed(() => [
  { label: '全部', value: 'all', count: learningTasks.value.length },
  { label: '待完成', value: 'todo', count: learningTasks.value.filter((task) => task.status !== 'done').length },
  { label: '已完成', value: 'done', count: taskStats.value.done },
  { label: '面试复盘', value: 'interview', count: learningTasks.value.filter((task) => task.taskType === 'interview').length },
]);

function learningPlanToBlocks(plan: LearningPlan): CareerPlanBlock[] {
  return [
    { title: '阶段任务', tasks: splitPlanText(plan.stageTasks) },
    { title: '技能补齐', tasks: splitPlanText(plan.skillTasks) },
    { title: '项目实践', tasks: splitPlanText(plan.projectTasks) },
    { title: '简历与面试', tasks: [...splitPlanText(plan.resumeTasks), ...splitPlanText(plan.interviewTasks)] },
  ];
}

function splitPlanText(value: string) {
  return value
    .split(/[;；。]/)
    .map((item) => item.trim())
    .filter(Boolean);
}

function priorityLabel(priority: string) {
  const labels: Record<string, string> = {
    high: '高优先级',
    medium: '中优先级',
    low: '低优先级',
  };
  return labels[priority] || priority || '中优先级';
}

function typeLabel(type: string) {
  const labels: Record<string, string> = {
    skill: '技能提升',
    project: '项目实践',
    resume: '简历优化',
    interview: '面试复盘',
  };
  return labels[type] || type || '成长任务';
}

function formatDate(value?: string) {
  if (!value) return '暂无日期';
  return value.replace('T', ' ').slice(0, 10);
}

function isMaterialized(task: LearningTask) {
  return materializedTaskIds.value.has(task.id);
}

async function loadWorkspace() {
  try {
    loading.value = true;
    const [profile, plan, tasks, materials] = await Promise.all([
      getProfileApi(),
      getCurrentLearningPlanApi(),
      getLearningTasksApi(),
      getResumeMaterialsApi(),
    ]);

    currentProfile.value = profile;
    learningPlan.value = plan;
    learningTasks.value = tasks;
    resumeMaterials.value = materials;
    if (plan) {
      planBlocks.value = learningPlanToBlocks(plan);
      careerPlanSource.value = plan.generationSource;
      careerPlanModel.value = plan.model || '';
      currentLevel.value = plan.goalSummary;
      generatedAt.value = '已读取现有计划';
      activeStep.value = progressPercent.value >= 80 ? 4 : progressPercent.value >= 50 ? 3 : 2;
    } else {
      planBlocks.value = [];
      currentLevel.value = '还没有学习计划。生成一次规划后，任务会持续沉淀到这里。';
      generatedAt.value = '尚未生成';
      activeStep.value = 1;
    }
  } catch {
    ElMessage.warning('学习闭环数据加载失败，请确认后端服务和登录状态');
  } finally {
    loading.value = false;
  }
}

async function handleRefreshPlan() {
  try {
    loading.value = true;
    currentProfile.value = await getProfileApi();
    const careerPlan = await generatePlanApi({
      stage: currentProfile.value.currentStage,
      targetPosition: currentProfile.value.targetPosition,
    });

    planBlocks.value = planToBlocks(careerPlan);
    careerPlanSource.value = careerPlan.generation_source;
    careerPlanModel.value = careerPlan.model || '';
    currentLevel.value = careerPlan.current_level;
    roadmapStages.value = careerPlan.roadmap;
    learningPlan.value = await generateLearningPlanApi();
    learningTasks.value = await getLearningTasksApi();
    resumeMaterials.value = await getResumeMaterialsApi();
    generatedAt.value = '刚刚重新生成';
    activeStep.value = 2;
    ElMessage.success('已重新生成职业规划和成长任务');
  } catch {
    ElMessage.warning('重新生成失败，请确认后端服务、数据库和登录状态');
  } finally {
    loading.value = false;
  }
}

async function handleCompleteTask(task: LearningTask) {
  if (task.status === 'done') return;

  try {
    taskLoadingId.value = task.id;
    const updatedTask = await completeLearningTaskApi(task.id);
    learningTasks.value = learningTasks.value.map((item) => (item.id === updatedTask.id ? updatedTask : item));
    if (learningPlan.value) {
      learningPlan.value.progress = progressPercent.value;
    }
    ElMessage.success('任务已完成打卡');
  } catch {
    ElMessage.warning('任务打卡失败，请确认后端服务是否可用');
  } finally {
    taskLoadingId.value = null;
  }
}

async function handleCreateMaterial(task: LearningTask) {
  if (task.status !== 'done') {
    ElMessage.warning('请先完成任务，再沉淀为简历素材');
    return;
  }
  if (isMaterialized(task)) {
    ElMessage.info('这条任务已经沉淀为简历素材');
    router.push('/resume/materials');
    return;
  }

  try {
    materializingTaskId.value = task.id;
    const material = await createMaterialFromLearningTaskApi(task.id);
    resumeMaterials.value = [material, ...resumeMaterials.value.filter((item) => item.id !== material.id)];
    ElMessage.success('已沉淀到简历素材库');
  } catch {
    ElMessage.warning('素材沉淀失败，请确认后端和数据库是否可用');
  } finally {
    materializingTaskId.value = null;
  }
}

async function handleCreateInterviewReviewTasks() {
  try {
    generatingReviewTasks.value = true;
    const result = await createInterviewImprovementPlanApi();
    const taskMap = new Map<number, LearningTask>();
    [...result.tasks, ...learningTasks.value].forEach((task) => taskMap.set(task.id, task));
    learningTasks.value = Array.from(taskMap.values());
    activeTaskFilter.value = 'interview';
    ElMessage.success('已根据面试复盘生成提升任务');
  } catch {
    ElMessage.warning('请先完成一次模拟面试，再生成复盘任务');
  } finally {
    generatingReviewTasks.value = false;
  }
}

onMounted(loadWorkspace);
</script>

<template>
  <section class="page-stack">
    <div class="module-hero">
      <div>
        <p class="eyebrow">Learning Loop</p>
        <h1>从面试复盘回流到成长任务</h1>
        <p class="page-intro">
          这里集中管理学习计划、面试复盘任务和简历素材沉淀，保证每一次训练结果都能回到求职材料中。
        </p>
      </div>
      <div class="module-hero__status">
        <span>闭环进度</span>
        <strong>{{ taskStats.done }}/{{ taskStats.total }} 已完成</strong>
        <em>{{ taskStats.materialized }} 条已沉淀为素材 · {{ generatedAt }}</em>
      </div>
    </div>

    <el-card shadow="never" class="module-card plan-timeline" v-loading="loading">
      <template #header>
        <div class="section-header">
          <span>阶段目标总览</span>
          <div class="header-actions">
            <div v-if="learningPlan" class="plan-meta">
              <el-tag :type="careerPlanSource === 'llm' ? 'success' : 'warning'">
                {{ careerPlanSource === 'llm' ? '大模型规划' : '本地降级' }}
              </el-tag>
              <small v-if="careerPlanModel">{{ careerPlanModel }}</small>
            </div>
            <el-button plain :loading="generatingReviewTasks" @click="handleCreateInterviewReviewTasks">
              生成面试复盘任务
            </el-button>
            <el-button type="primary" plain :loading="loading" @click="handleRefreshPlan">
              重新生成规划
            </el-button>
          </div>
        </div>
      </template>

      <el-steps :active="activeStep" finish-status="success" align-center>
        <el-step title="职业认知" />
        <el-step title="技能学习" />
        <el-step title="项目积累" />
        <el-step title="简历优化" />
        <el-step title="模拟面试" />
      </el-steps>

      <div class="career-plan-context">
        <p>{{ currentLevel }}</p>
        <div v-if="roadmapStages.length" class="roadmap-stages">
          <span v-for="(stage, index) in roadmapStages" :key="`${index}-${stage}`">
            {{ index + 1 }}. {{ stage }}
          </span>
        </div>
      </div>
    </el-card>

    <el-row v-if="planBlocks.length" :gutter="22">
      <el-col v-for="block in planBlocks" :key="block.title" :xs="24" :md="12" :lg="6">
        <el-card shadow="never" class="module-card stage-card">
          <template #header>{{ block.title }}</template>
          <ul class="clean-list">
            <li v-for="task in block.tasks" :key="task">{{ task }}</li>
          </ul>
        </el-card>
      </el-col>
    </el-row>

    <div class="module-grid module-grid--side">
      <el-card shadow="never" class="module-card">
        <template #header>
          <div class="section-header">
            <span>学习路线拆解</span>
            <div v-if="learningPlan" class="plan-meta">
              <el-tag :type="learningPlan.generationSource === 'llm' ? 'success' : 'warning'">
                {{ learningPlan.generationSource === 'llm' ? '大模型生成' : '本地降级' }}
              </el-tag>
              <el-tag type="info">{{ learningPlan.totalPeriod }} · 每周 {{ learningPlan.weeklyHours }} 小时</el-tag>
            </div>
          </div>
        </template>

        <template v-if="learningPlan">
          <div class="mock-result">
            <span>目标摘要</span>
            <p>{{ learningPlan.goalSummary }}</p>
          </div>
          <el-descriptions :column="1" border>
            <el-descriptions-item label="阶段任务">{{ learningPlan.stageTasks }}</el-descriptions-item>
            <el-descriptions-item label="技能补齐">{{ learningPlan.skillTasks }}</el-descriptions-item>
            <el-descriptions-item label="项目实践">{{ learningPlan.projectTasks }}</el-descriptions-item>
            <el-descriptions-item label="简历沉淀">{{ learningPlan.resumeTasks }}</el-descriptions-item>
            <el-descriptions-item label="面试训练">{{ learningPlan.interviewTasks }}</el-descriptions-item>
          </el-descriptions>
        </template>
        <el-empty v-else description="还没有学习计划">
          <el-button type="primary" :loading="loading" @click="handleRefreshPlan">生成学习计划</el-button>
        </el-empty>
      </el-card>

      <el-card shadow="never" class="module-card">
        <template #header>
          <div class="section-header">
            <span>成长任务</span>
            <el-progress :percentage="progressPercent" :stroke-width="8" style="width: 150px" />
          </div>
        </template>

        <div class="task-filters">
          <button
            v-for="item in filterItems"
            :key="item.value"
            type="button"
            :class="{ active: activeTaskFilter === item.value }"
            @click="activeTaskFilter = item.value as typeof activeTaskFilter"
          >
            {{ item.label }} {{ item.count }}
          </button>
        </div>

        <div v-if="filteredTasks.length" class="task-stack">
          <article
            v-for="task in filteredTasks"
            :key="task.id"
            class="task-item"
            :class="{ 'task-item--done': task.status === 'done' }"
          >
            <div>
              <div class="task-tags">
                <el-tag size="small" :type="task.taskType === 'interview' ? 'success' : 'info'">
                  {{ typeLabel(task.taskType) }}
                </el-tag>
                <el-tag size="small" :type="task.priority === 'high' ? 'danger' : 'info'">
                  {{ priorityLabel(task.priority) }}
                </el-tag>
                <el-tag v-if="isMaterialized(task)" size="small" type="success">已沉淀素材</el-tag>
              </div>
              <strong>{{ task.title }}</strong>
              <p>{{ task.description }}</p>
              <small>截止：{{ formatDate(task.dueDate) }}</small>
            </div>
            <div class="task-actions">
              <el-button
                size="small"
                :type="task.status === 'done' ? 'success' : 'primary'"
                :loading="taskLoadingId === task.id"
                @click="handleCompleteTask(task)"
              >
                {{ task.status === 'done' ? '已完成' : '完成打卡' }}
              </el-button>
              <el-button
                size="small"
                plain
                type="primary"
                :disabled="task.status !== 'done'"
                :loading="materializingTaskId === task.id"
                @click="handleCreateMaterial(task)"
              >
                {{ isMaterialized(task) ? '查看素材' : '沉淀为素材' }}
              </el-button>
            </div>
          </article>
        </div>
        <el-empty v-else description="暂无匹配的成长任务" />
      </el-card>
    </div>
  </section>
</template>

<style scoped>
.section-header,
.header-actions,
.plan-meta,
.career-plan-actions,
.career-plan-context,
.roadmap-stages,
.task-tags {
  display: flex;
  align-items: center;
  gap: 10px;
}

.section-header {
  justify-content: space-between;
}

.header-actions,
.plan-meta,
.roadmap-stages,
.task-tags {
  flex-wrap: wrap;
}

.header-actions,
.plan-meta {
  justify-content: flex-end;
}

.plan-meta small {
  color: #6b7280;
}

.career-plan-context {
  margin-top: 22px;
  padding-top: 18px;
  border-top: 1px solid #e5e7eb;
  align-items: flex-start;
  flex-direction: column;
}

.career-plan-context p {
  margin: 0;
  color: #374151;
  line-height: 1.7;
}

.roadmap-stages span {
  font-size: 13px;
  color: #475569;
  padding-right: 10px;
  border-right: 1px solid #cbd5e1;
}

.task-filters {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 14px;
}

.task-filters button {
  border: 1px solid #dbe3ef;
  border-radius: 8px;
  padding: 7px 10px;
  background: #fff;
  color: #475569;
  cursor: pointer;
}

.task-filters button.active {
  border-color: #2563eb;
  background: #eff6ff;
  color: #2563eb;
}

.task-stack {
  display: grid;
  gap: 12px;
}

.task-item {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 120px;
  gap: 14px;
  align-items: center;
  padding: 14px;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  background: #fff;
}

.task-item strong {
  display: block;
  margin: 8px 0 4px;
}

.task-item p {
  margin: 0 0 6px;
  color: #4b5563;
  line-height: 1.6;
}

.task-item small {
  color: #6b7280;
}

.task-actions {
  display: flex;
  flex-direction: column;
  gap: 8px;
  align-items: stretch;
}

.task-item--done {
  background: #f0fdf4;
  border-color: #bbf7d0;
}

@media (max-width: 720px) {
  .section-header {
    align-items: flex-start;
    flex-direction: column;
  }

  .task-item {
    grid-template-columns: 1fr;
  }
}
</style>
