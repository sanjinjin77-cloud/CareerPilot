<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';

import {
  analyzeJDApi,
  customizeResumeApi,
  getCurrentTargetOfferApi,
  updateResumeDocumentApi,
} from '@/api/career';
import type { JDAnalysis, ResumeDocument, TargetOffer } from '@/types';

const router = useRouter();

const jdForm = reactive({
  jobTitle: '',
  companyName: '',
  jdContent: '',
});

const currentTargetOffer = ref<TargetOffer | null>(null);
const analysis = ref<JDAnalysis | null>(null);
const resumeDraft = ref<ResumeDocument | null>(null);
const loadingTarget = ref(false);
const analyzing = ref(false);
const generating = ref(false);
const finalizing = ref(false);

const scoreText = computed(() => (analysis.value ? `${analysis.value.matchScore}%` : '--'));
const keywords = computed(() => analysis.value?.keywords ?? []);
const recommendedMaterials = computed(() => analysis.value?.recommendedMaterials ?? []);
const requirements = computed(() => analysis.value?.requirements ?? []);
const suggestions = computed(() => analysis.value?.suggestions ?? ['等待分析 JD 后生成匹配建议']);
const targetSummary = computed(() => {
  if (!currentTargetOffer.value) return '尚未设置目标 Offer';
  return `${currentTargetOffer.value.companyName} · ${currentTargetOffer.value.jobTitle}`;
});
const analysisSourceLabel = computed(() => {
  if (!analysis.value) return '等待分析';
  return analysis.value.analysisSource === 'llm' ? '大模型分析' : '本地降级';
});
const resumeSourceLabel = computed(() => {
  if (!resumeDraft.value) return '';
  return resumeDraft.value.generationSource === 'llm' ? '大模型生成' : '本地降级';
});

function buildJdFromTargetOffer(offer: TargetOffer) {
  const blocks = [
    offer.jobRequirements,
    offer.requiredSkills ? `核心技能：${offer.requiredSkills}` : '',
    offer.targetCity ? `目标城市：${offer.targetCity}` : '',
    offer.targetSalary ? `目标薪资：${offer.targetSalary}` : '',
  ].filter(Boolean);

  return blocks.join('\n\n');
}

function fillFromTargetOffer() {
  if (!currentTargetOffer.value) {
    ElMessage.warning('请先在岗位匹配或岗位筛选中设置目标 Offer');
    router.push('/career/job-match');
    return;
  }

  jdForm.jobTitle = currentTargetOffer.value.jobTitle;
  jdForm.companyName = currentTargetOffer.value.companyName;
  jdForm.jdContent = buildJdFromTargetOffer(currentTargetOffer.value);
  analysis.value = null;
  resumeDraft.value = null;
}

async function loadTargetOffer() {
  try {
    loadingTarget.value = true;
    currentTargetOffer.value = await getCurrentTargetOfferApi();
    if (currentTargetOffer.value) {
      fillFromTargetOffer();
    }
  } catch {
    ElMessage.warning('当前目标 Offer 读取失败，可先手动粘贴 JD 继续分析');
  } finally {
    loadingTarget.value = false;
  }
}

async function handleAnalyzeJD() {
  if (!jdForm.jobTitle.trim() || !jdForm.companyName.trim() || !jdForm.jdContent.trim()) {
    ElMessage.warning('请补全岗位、公司和 JD 内容');
    return;
  }

  try {
    analyzing.value = true;
    analysis.value = await analyzeJDApi(jdForm);
    resumeDraft.value = null;
    ElMessage.success('已完成 JD 匹配分析');
  } catch {
    ElMessage.error('JD 分析失败，请确认已登录且后端服务可用');
  } finally {
    analyzing.value = false;
  }
}

async function handleCustomizeResume() {
  if (!jdForm.jobTitle.trim() || !jdForm.companyName.trim() || !jdForm.jdContent.trim()) {
    ElMessage.warning('请补全岗位、公司和 JD 内容');
    return;
  }

  try {
    generating.value = true;
    if (!analysis.value) {
      analysis.value = await analyzeJDApi(jdForm);
    }
    resumeDraft.value = await customizeResumeApi(jdForm);
    ElMessage.success('已生成定制简历草稿');
  } catch {
    ElMessage.error('定制简历生成失败，请先完善素材库或确认后端服务可用');
  } finally {
    generating.value = false;
  }
}

async function setGeneratedResumeAsFinal() {
  if (!resumeDraft.value) {
    ElMessage.warning('请先生成一份定制简历');
    return;
  }

  try {
    finalizing.value = true;
    resumeDraft.value = await updateResumeDocumentApi(resumeDraft.value.id, {
      title: resumeDraft.value.title,
      content: resumeDraft.value.content,
      optimizedContent: resumeDraft.value.optimizedContent,
      status: 'final',
    });
    ElMessage.success('已设为最终版，可用于模拟面试');
  } catch {
    ElMessage.error('设为最终版失败，请确认后端服务可用');
  } finally {
    finalizing.value = false;
  }
}

onMounted(loadTargetOffer);
</script>

<template>
  <section class="page-stack">
    <div class="module-hero">
      <div>
        <p class="eyebrow">JD Analysis</p>
        <h1>用目标 Offer 反推简历表达</h1>
        <p class="page-intro">
          系统会优先读取当前目标 Offer，把岗位要求转成 JD 分析，并从真实素材库中推荐可写入简历的经历。
        </p>
      </div>
      <div class="module-hero__status">
        <span>当前目标</span>
        <strong>{{ targetSummary }}</strong>
        <em>最近匹配度 {{ currentTargetOffer?.matchScore ?? 0 }}%</em>
      </div>
    </div>

    <el-alert
      v-if="!currentTargetOffer && !loadingTarget"
      type="warning"
      show-icon
      :closable="false"
      title="尚未设置目标 Offer，可以先去岗位匹配页选择一个岗位，或在下方手动填写 JD。"
    >
      <template #default>
        <el-button size="small" type="primary" plain @click="router.push('/career/job-match')">
          去设置目标 Offer
        </el-button>
      </template>
    </el-alert>

    <div class="module-grid module-grid--side">
      <el-card shadow="never" class="module-card jd-input-card" v-loading="loadingTarget">
        <template #header>
          <div class="section-header">
            <span>岗位 JD</span>
            <el-button size="small" :disabled="!currentTargetOffer" @click="fillFromTargetOffer">
              带入当前目标 Offer
            </el-button>
          </div>
        </template>

        <el-form label-position="top">
          <el-form-item label="目标岗位">
            <el-input v-model="jdForm.jobTitle" placeholder="例如：前端开发工程师" />
          </el-form-item>
          <el-form-item label="目标公司">
            <el-input v-model="jdForm.companyName" placeholder="例如：目标公司" />
          </el-form-item>
          <el-form-item label="JD 内容">
            <el-input
              v-model="jdForm.jdContent"
              type="textarea"
              :rows="11"
              placeholder="粘贴岗位职责、任职要求、技术栈、业务方向等内容"
            />
          </el-form-item>
        </el-form>

        <div class="action-row">
          <el-button type="primary" :loading="analyzing" @click="handleAnalyzeJD">分析 JD</el-button>
          <el-button type="success" plain :loading="generating" @click="handleCustomizeResume">
            生成定制简历
          </el-button>
        </div>
      </el-card>

      <el-card shadow="never" class="module-card">
        <template #header>
          <div class="section-header">
            <span>匹配预览</span>
            <div class="tag-row">
              <el-tag size="small" :type="analysis?.analysisSource === 'llm' ? 'success' : 'warning'">
                {{ analysisSourceLabel }}
              </el-tag>
              <small v-if="analysis?.model">{{ analysis.model }}</small>
            </div>
          </div>
        </template>

        <div class="score-panel">
          <span>简历匹配度</span>
          <strong>{{ scoreText }}</strong>
        </div>

        <section class="result-block">
          <h3>关键词</h3>
          <div class="keyword-cloud">
            <el-tag v-for="keyword in keywords" :key="keyword">{{ keyword }}</el-tag>
            <span v-if="!keywords.length">暂无关键词</span>
          </div>
        </section>

        <section class="result-block">
          <h3>岗位要求</h3>
          <ul class="clean-list jd-suggestions">
            <li v-for="requirement in requirements" :key="requirement">{{ requirement }}</li>
            <li v-if="!requirements.length">等待分析后提取岗位要求</li>
          </ul>
        </section>

        <section class="result-block">
          <h3>推荐写入简历</h3>
          <p>{{ recommendedMaterials.length ? recommendedMaterials.join('、') : '暂无匹配素材' }}</p>
        </section>

        <section class="result-block">
          <h3>优化建议</h3>
          <ul class="clean-list jd-suggestions">
            <li v-for="suggestion in suggestions" :key="suggestion">{{ suggestion }}</li>
          </ul>
        </section>
      </el-card>
    </div>

    <el-card v-if="resumeDraft" shadow="never" class="module-card">
      <template #header>
        <div class="section-header">
          <span>{{ resumeDraft.title }}</span>
          <div class="tag-row">
            <el-tag :type="resumeDraft.generationSource === 'llm' ? 'success' : 'warning'">
              {{ resumeSourceLabel }}
            </el-tag>
            <el-tag type="info">{{ resumeDraft.status }}</el-tag>
            <small v-if="resumeDraft.model">{{ resumeDraft.model }}</small>
          </div>
        </div>
      </template>

      <div class="resume-draft-grid">
        <section>
          <h3>匹配素材</h3>
          <div class="keyword-cloud">
            <el-tag v-for="material in resumeDraft.matchedMaterials" :key="material" type="info">
              {{ material }}
            </el-tag>
          </div>
        </section>
        <section>
          <h3>关键词</h3>
          <div class="keyword-cloud">
            <el-tag v-for="keyword in resumeDraft.keywords" :key="keyword">{{ keyword }}</el-tag>
          </div>
        </section>
      </div>

      <div class="resume-draft">
        <span>定制简历草稿</span>
        <pre>{{ resumeDraft.optimizedContent }}</pre>
      </div>

      <div class="resume-actions">
        <el-button type="success" :loading="finalizing" @click="setGeneratedResumeAsFinal">
          设为最终版
        </el-button>
        <el-button @click="router.push('/resume/templates')">查看草稿管理</el-button>
        <el-button type="primary" plain @click="router.push('/interview')">进入模拟面试</el-button>
      </div>
    </el-card>
  </section>
</template>

<style scoped>
.section-header,
.tag-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
}

.tag-row {
  justify-content: flex-end;
  flex-wrap: wrap;
}

.tag-row small {
  color: #64748b;
}

.score-panel {
  display: grid;
  gap: 4px;
  padding: 16px;
  margin-bottom: 18px;
  border-radius: 8px;
  background: #eff6ff;
  color: #2563eb;
}

.score-panel span {
  font-size: 13px;
}

.score-panel strong {
  font-size: 34px;
  line-height: 1;
}

.result-block {
  display: grid;
  gap: 10px;
  padding: 14px 0;
  border-top: 1px solid #e2e8f0;
}

.result-block h3 {
  margin: 0;
  font-size: 15px;
}

.result-block p {
  margin: 0;
  line-height: 1.7;
  color: #475569;
}

.keyword-cloud {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  color: #64748b;
}

.resume-draft-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 18px;
  margin-bottom: 18px;
}

.resume-draft-grid h3 {
  margin: 0 0 10px;
  font-size: 15px;
}

.resume-draft {
  display: grid;
  gap: 10px;
}

.resume-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 16px;
}

.resume-draft span {
  font-weight: 700;
}

.resume-draft pre {
  margin: 0;
  padding: 16px;
  overflow: auto;
  white-space: pre-wrap;
  line-height: 1.7;
  border-radius: 8px;
  background: #f8fafc;
  color: #1f2937;
}

@media (max-width: 720px) {
  .section-header {
    align-items: flex-start;
    flex-direction: column;
  }

  .resume-draft-grid {
    grid-template-columns: 1fr;
  }
}
</style>
