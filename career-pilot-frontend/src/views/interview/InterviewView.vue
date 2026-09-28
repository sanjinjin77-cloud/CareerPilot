<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue';
import { ElMessage } from 'element-plus';

import {
  createInterviewImprovementPlanApi,
  deleteInterviewRecordApi,
  generateInterviewQuestionApi,
  getCurrentTargetOfferApi,
  getInterviewRecordsApi,
  getResumeDocumentsApi,
  submitInterviewAnswerApi,
} from '@/api/career';
import type {
  InterviewImprovementPlan,
  InterviewQuestion,
  InterviewRecord,
  ResumeDocument,
  TargetOffer,
} from '@/types';

const jobTitle = ref('Frontend Engineer');
const selectedResumeDocumentId = ref<number | undefined>();
const answer = ref('');
const currentQuestion = ref<InterviewQuestion | null>(null);
const feedback = ref('生成问题后，系统会基于目标岗位和所选简历进行追问。');
const scores = reactive({
  technical: 0,
  project: 0,
  logic: 0,
  star: 0,
});
const round = ref(0);
const targetOffer = ref<TargetOffer | null>(null);
const resumeDocuments = ref<ResumeDocument[]>([]);
const interviewRecords = ref<InterviewRecord[]>([]);
const loadingResumes = ref(false);
const loadingRecords = ref(false);
const loadingQuestion = ref(false);
const submitting = ref(false);
const generatingImprovement = ref(false);
const deletingRecordId = ref<number | null>(null);
const hasSubmittedAnswer = ref(false);
const improvementPlan = ref<InterviewImprovementPlan | null>(null);
const evaluationSource = ref<'llm' | 'local_fallback' | 'history' | null>(null);
const evaluationModel = ref('');

const selectedResumeDocument = computed(
  () => resumeDocuments.value.find((document) => document.id === selectedResumeDocumentId.value) || null,
);
const finalResumeDocument = computed(() => resumeDocuments.value.find((document) => document.status === 'final') || null);
const questionText = computed(() =>
  currentQuestion.value?.question ?? '点击“生成问题”开始一次真实模拟面试。',
);
const resumeTitle = computed(
  () => currentQuestion.value?.resumeTitle || selectedResumeDocument.value?.title || '尚未关联定制简历',
);
const keywordText = computed(() => currentQuestion.value?.keywords.join('、') || '等待生成');
const questionSourceLabel = computed(() => {
  if (!currentQuestion.value) return '等待生成';
  return currentQuestion.value.generationSource === 'llm' ? '大模型生成' : '本地降级';
});
const evaluationSourceLabel = computed(() => {
  if (!evaluationSource.value) return '等待评分';
  if (evaluationSource.value === 'history') return '历史记录';
  return evaluationSource.value === 'llm' ? '大模型评分' : '本地降级评分';
});
const feedbackTitle = computed(() => (evaluationSource.value === 'llm' ? 'AI 反馈' : '面试反馈'));
const averageScore = computed(() => {
  const values = Object.values(scores);
  const sum = values.reduce((total, value) => total + value, 0);
  return Math.round(sum / values.length);
});
const trainingContext = computed(() => {
  const target = targetOffer.value
    ? `${targetOffer.value.companyName} · ${targetOffer.value.jobTitle}`
    : jobTitle.value;
  const resume = finalResumeDocument.value ? `最终版：${finalResumeDocument.value.title}` : resumeTitle.value;
  return `${target} / ${resume}`;
});

function statusLabel(status: string) {
  const labels: Record<string, string> = {
    draft: '草稿',
    final: '最终版',
    archived: '已归档',
  };
  return labels[status] || status || '草稿';
}

function inferJobTitleFromDocument(document: ResumeDocument) {
  const separator = ' - ';
  const suffix = ' 定制简历';
  const separatorIndex = document.title.indexOf(separator);
  const suffixIndex = document.title.indexOf(suffix);
  if (separatorIndex < 0 || suffixIndex <= separatorIndex) return '';
  return document.title.slice(separatorIndex + separator.length, suffixIndex).trim();
}

function formatTime(value?: string) {
  if (!value) return '暂无时间';
  return value.replace('T', ' ').slice(0, 16);
}

async function loadTargetOffer() {
  try {
    targetOffer.value = await getCurrentTargetOfferApi();
    if (targetOffer.value) {
      jobTitle.value = targetOffer.value.jobTitle;
    }
  } catch {
    targetOffer.value = null;
  }
}

async function loadResumeDocuments() {
  try {
    loadingResumes.value = true;
    const documents = await getResumeDocumentsApi();
    resumeDocuments.value = documents.filter((document) => document.status !== 'archived');
    const preferred = finalResumeDocument.value || resumeDocuments.value[0];
    if (preferred) {
      selectedResumeDocumentId.value = preferred.id;
      if (!targetOffer.value) {
        const inferred = inferJobTitleFromDocument(preferred);
        if (inferred) jobTitle.value = inferred;
      }
    }
  } catch {
    ElMessage.warning('简历草稿加载失败，面试会使用后端默认简历上下文');
  } finally {
    loadingResumes.value = false;
  }
}

async function loadInterviewRecords() {
  try {
    loadingRecords.value = true;
    interviewRecords.value = await getInterviewRecordsApi();
  } catch {
    ElMessage.warning('面试历史加载失败');
  } finally {
    loadingRecords.value = false;
  }
}

function resetAnswerState() {
  answer.value = '';
  hasSubmittedAnswer.value = false;
  improvementPlan.value = null;
  evaluationSource.value = null;
  evaluationModel.value = '';
  scores.technical = 0;
  scores.project = 0;
  scores.logic = 0;
  scores.star = 0;
}

async function handleGenerateQuestion() {
  try {
    loadingQuestion.value = true;
    currentQuestion.value = await generateInterviewQuestionApi({
      jobTitle: jobTitle.value,
      resumeDocumentId: selectedResumeDocumentId.value,
    });
    jobTitle.value = currentQuestion.value.jobTitle;
    if (currentQuestion.value.resumeDocumentId) {
      selectedResumeDocumentId.value = currentQuestion.value.resumeDocumentId;
    }
    resetAnswerState();
    feedback.value = '新问题已生成，请结合所选简历中的真实经历作答。';
    round.value += 1;
  } catch {
    ElMessage.error('生成面试问题失败，请确认已登录且后端服务可用');
  } finally {
    loadingQuestion.value = false;
  }
}

async function handleSubmitAnswer() {
  if (!answer.value.trim()) {
    ElMessage.warning('请先输入面试回答');
    return;
  }

  try {
    submitting.value = true;
    const result = await submitInterviewAnswerApi({
      jobTitle: jobTitle.value,
      resumeDocumentId: currentQuestion.value?.resumeDocumentId || selectedResumeDocumentId.value,
      question: questionText.value,
      answer: answer.value,
    });

    scores.technical = result.scores.technical;
    scores.project = result.scores.project;
    scores.logic = result.scores.logic;
    scores.star = result.scores.star;
    feedback.value = result.feedback;
    evaluationSource.value = result.evaluationSource;
    evaluationModel.value = result.model || '';
    hasSubmittedAnswer.value = true;
    await loadInterviewRecords();
    ElMessage.success('已保存面试复盘记录');
  } catch {
    ElMessage.error('提交回答失败，请确认后端服务和登录状态');
  } finally {
    submitting.value = false;
  }
}

function showHistoryRecord(record: InterviewRecord) {
  currentQuestion.value = {
    jobTitle: record.jobTitle,
    resumeDocumentId: record.resumeDocumentId,
    resumeTitle: record.resumeTitle || '',
    question: record.question,
    keywords: [],
    generationSource: 'local_fallback',
    model: '',
  };
  jobTitle.value = record.jobTitle;
  if (record.resumeDocumentId) selectedResumeDocumentId.value = record.resumeDocumentId;
  answer.value = record.answer;
  scores.technical = record.scores.technical;
  scores.project = record.scores.project;
  scores.logic = record.scores.logic;
  scores.star = record.scores.star;
  feedback.value = record.feedback;
  evaluationSource.value = record.evaluationSource;
  evaluationModel.value = record.model || '';
  hasSubmittedAnswer.value = true;
  improvementPlan.value = null;
}

async function handleDeleteRecord(record: InterviewRecord) {
  if (!record.id) return;
  try {
    deletingRecordId.value = record.id;
    await deleteInterviewRecordApi(record.id);
    interviewRecords.value = interviewRecords.value.filter((item) => item.id !== record.id);
    ElMessage.success('面试记录已删除');
  } catch {
    ElMessage.error('删除面试记录失败，请确认后端服务可用');
  } finally {
    deletingRecordId.value = null;
  }
}

async function handleCreateImprovementPlan() {
  try {
    generatingImprovement.value = true;
    improvementPlan.value = await createInterviewImprovementPlanApi();
    ElMessage.success('提升任务已加入学习计划');
  } catch {
    ElMessage.error('生成提升任务失败，请先完成一次模拟面试');
  } finally {
    generatingImprovement.value = false;
  }
}

onMounted(async () => {
  await loadTargetOffer();
  await Promise.all([loadResumeDocuments(), loadInterviewRecords()]);
  handleGenerateQuestion();
});
</script>

<template>
  <section class="page-stack">
    <div class="module-hero">
      <div>
        <p class="eyebrow">Mock Interview</p>
        <h1>围绕目标岗位进行真实追问训练</h1>
        <p class="page-intro">
          系统会结合当前目标 Offer、最终版简历和回答内容生成追问与评分，并把复盘结果沉淀为学习任务。
        </p>
      </div>
      <div class="module-hero__status">
        <span>训练上下文</span>
        <strong>{{ trainingContext }}</strong>
        <em>第 {{ round }} 轮 · {{ feedback }}</em>
      </div>
    </div>

    <el-alert
      v-if="!finalResumeDocument && resumeDocuments.length"
      type="warning"
      show-icon
      :closable="false"
      title="当前还没有最终版简历，系统会使用最新草稿。建议在简历草稿页先设为最终版。"
    />

    <div class="module-grid module-grid--side">
      <el-card shadow="never" class="module-card interview-card">
        <template #header>当前问题</template>
        <el-form label-position="top">
          <el-form-item label="面试简历">
            <el-select
              v-model="selectedResumeDocumentId"
              clearable
              filterable
              :loading="loadingResumes"
              placeholder="选择一份草稿或最终版"
            >
              <el-option
                v-for="document in resumeDocuments"
                :key="document.id"
                :label="`${document.title} · ${statusLabel(document.status)}`"
                :value="document.id"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="目标岗位">
            <el-input v-model="jobTitle" />
          </el-form-item>
        </el-form>

        <div class="interview-context">
          <span>关联简历：{{ resumeTitle }}</span>
          <span>关键词：{{ keywordText }}</span>
          <span class="source-row">
            问题来源：
            <el-tag
              size="small"
              :type="currentQuestion?.generationSource === 'llm' ? 'success' : 'warning'"
            >
              {{ questionSourceLabel }}
            </el-tag>
            <small v-if="currentQuestion?.model">{{ currentQuestion.model }}</small>
          </span>
        </div>

        <p class="question">{{ questionText }}</p>
        <el-input
          v-model="answer"
          type="textarea"
          :rows="7"
          placeholder="请输入你的回答，建议包含背景、任务、行动和结果"
        />
        <div class="action-row">
          <el-button type="primary" :loading="submitting" @click="handleSubmitAnswer">提交回答</el-button>
          <el-button :loading="loadingQuestion" @click="handleGenerateQuestion">换一道问题</el-button>
        </div>
      </el-card>

      <el-card shadow="never" class="module-card">
        <template #header>评分维度</template>
        <div class="score-summary">
          <strong>{{ averageScore }}%</strong>
          <span>综合表现</span>
        </div>
        <div class="score-list">
          <span>技术能力 <strong>{{ scores.technical }}%</strong></span>
          <span>项目理解 <strong>{{ scores.project }}%</strong></span>
          <span>表达逻辑 <strong>{{ scores.logic }}%</strong></span>
          <span>STAR 法则 <strong>{{ scores.star }}%</strong></span>
        </div>
        <div class="interview-feedback">
          <div class="feedback-heading">
            <span>{{ feedbackTitle }}</span>
            <div>
              <el-tag size="small" :type="evaluationSource === 'llm' ? 'success' : 'warning'">
                {{ evaluationSourceLabel }}
              </el-tag>
              <small v-if="evaluationModel">{{ evaluationModel }}</small>
            </div>
          </div>
          <p>{{ feedback }}</p>
        </div>
        <el-button
          v-if="hasSubmittedAnswer"
          class="improvement-action"
          type="success"
          plain
          :loading="generatingImprovement"
          @click="handleCreateImprovementPlan"
        >
          生成提升任务
        </el-button>
      </el-card>
    </div>

    <el-card v-if="improvementPlan" shadow="never" class="module-card improvement-panel">
      <template #header>
        <div class="improvement-header">
          <span>面试复盘与求职提升</span>
          <el-tag type="success">已回流学习计划</el-tag>
        </div>
      </template>

      <div class="improvement-overview">
        <div>
          <span>最近平均表现</span>
          <strong>{{ improvementPlan.averageScore }}%</strong>
        </div>
        <p>{{ improvementPlan.summary }}</p>
      </div>

      <div class="focus-list">
        <el-tag v-for="area in improvementPlan.focusAreas" :key="area" type="warning">
          {{ area }}
        </el-tag>
      </div>

      <div class="improvement-tasks">
        <article v-for="task in improvementPlan.tasks" :key="task.id">
          <div>
            <strong>{{ task.title }}</strong>
            <el-tag size="small" :type="task.priority === 'high' ? 'danger' : 'info'">
              {{ task.priority }}
            </el-tag>
          </div>
          <p>{{ task.description }}</p>
          <span>截止日期：{{ task.dueDate }}</span>
        </article>
      </div>
    </el-card>

    <el-card shadow="never" class="module-card history-panel" v-loading="loadingRecords">
      <template #header>
        <div class="history-header">
          <span>历史复盘</span>
          <el-button size="small" @click="loadInterviewRecords">刷新</el-button>
        </div>
      </template>

      <el-empty v-if="!interviewRecords.length && !loadingRecords" description="暂无面试记录" />

      <div v-else class="history-list">
        <article v-for="record in interviewRecords" :key="record.id">
          <div class="history-item__main" @click="showHistoryRecord(record)">
            <div>
              <strong>{{ record.jobTitle }}</strong>
              <span>{{ formatTime(record.createdAt) }}</span>
            </div>
            <p>{{ record.question }}</p>
            <em>技术得分 {{ record.scores.technical }}%</em>
          </div>
          <el-button
            size="small"
            type="danger"
            plain
            :loading="deletingRecordId === record.id"
            @click="handleDeleteRecord(record)"
          >
            删除
          </el-button>
        </article>
      </div>
    </el-card>
  </section>
</template>

<style scoped>
.interview-context {
  display: grid;
  gap: 8px;
  margin-bottom: 14px;
  padding: 12px;
  border-radius: 8px;
  background: #f8fafc;
  color: #475569;
  font-size: 14px;
}

.source-row,
.feedback-heading,
.feedback-heading > div {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}

.source-row small,
.feedback-heading small {
  color: #64748b;
}

.score-summary {
  display: flex;
  align-items: baseline;
  gap: 10px;
  margin-bottom: 16px;
}

.score-summary strong {
  color: #2563eb;
  font-size: 28px;
}

.score-summary span {
  color: #64748b;
}

.interview-feedback {
  margin-top: 18px;
  padding: 14px;
  border-radius: 8px;
  background: #f8fafc;
}

.interview-feedback span {
  font-weight: 700;
}

.feedback-heading {
  justify-content: space-between;
}

.interview-feedback p {
  margin: 8px 0 0;
  line-height: 1.7;
  color: #334155;
}

.improvement-action {
  width: 100%;
  margin-top: 14px;
}

.improvement-header,
.history-header,
.improvement-tasks article > div {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.history-header {
  justify-content: space-between;
}

.improvement-overview {
  display: grid;
  grid-template-columns: 150px minmax(0, 1fr);
  align-items: center;
  gap: 20px;
}

.improvement-overview > div {
  display: grid;
  gap: 4px;
}

.improvement-overview span,
.improvement-tasks article > span {
  color: #64748b;
  font-size: 13px;
}

.improvement-overview strong {
  color: #2563eb;
  font-size: 28px;
}

.improvement-overview p,
.improvement-tasks p {
  margin: 0;
  line-height: 1.65;
  color: #334155;
}

.focus-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin: 18px 0;
}

.improvement-tasks {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
}

.improvement-tasks article {
  display: grid;
  gap: 10px;
  padding: 14px;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
}

.history-list {
  display: grid;
  gap: 12px;
}

.history-list article {
  display: grid;
  grid-template-columns: minmax(0, 1fr) auto;
  gap: 14px;
  align-items: center;
  padding: 14px;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
}

.history-item__main {
  display: grid;
  gap: 8px;
  cursor: pointer;
}

.history-item__main > div {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.history-item__main span,
.history-item__main em {
  color: #64748b;
  font-size: 13px;
  font-style: normal;
}

.history-item__main p {
  margin: 0;
  color: #334155;
  line-height: 1.6;
}

@media (max-width: 860px) {
  .improvement-overview,
  .improvement-tasks {
    grid-template-columns: 1fr;
  }

  .history-list article,
  .history-item__main > div {
    grid-template-columns: 1fr;
    align-items: start;
  }
}
</style>
