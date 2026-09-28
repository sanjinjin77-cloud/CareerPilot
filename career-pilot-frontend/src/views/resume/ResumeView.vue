<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';

import {
  createResumeMaterialApi,
  deleteResumeMaterialApi,
  getResumeMaterialsApi,
  optimizeResumeMaterialApi,
  updateResumeMaterialApi,
} from '@/api/career';
import { resumeMaterials } from '@/mock/careerPilotMock';
import { createResumeMaterialMock, optimizeResumeMaterialMock } from '@/services/mockCareerService';
import type { ResumeMaterial } from '@/types';

const typeOptions = [
  { label: '项目经历', value: 'project' },
  { label: '比赛经历', value: 'competition' },
  { label: '课程设计', value: 'course' },
  { label: '实习经历', value: 'internship' },
  { label: '技能成长', value: 'skill_growth' },
  { label: '面试复盘', value: 'interview_review' },
  { label: '简历优化', value: 'resume_refinement' },
  { label: '成长记录', value: 'growth_record' },
];

const materials = ref<ResumeMaterial[]>([]);
const loading = ref(false);
const submitting = ref(false);
const optimizingId = ref<number | null>(null);
const deletingId = ref<number | null>(null);
const editingId = ref<number | null>(null);
const usingFallback = ref(false);
const optimizedPreview = ref('');

const material = reactive({
  type: typeOptions[0].value,
  title: '',
  content: '',
  tagsText: '',
});

const materialCountText = computed(() => `${materials.value.length} 条`);
const formTitle = computed(() => (editingId.value ? '编辑素材' : '新增素材'));
const primaryActionText = computed(() => (editingId.value ? '保存修改' : '添加素材'));
const taskMaterialCount = computed(() => materials.value.filter((item) => item.sourceTaskId).length);
const categoryStats = computed(() =>
  typeOptions.map((option) => ({
    label: option.label,
    value: option.value,
    count: materials.value.filter((item) => item.type === option.value || item.type === option.label).length,
  })),
);

function typeLabel(type: string) {
  return typeOptions.find((option) => option.value === type || option.label === type)?.label || type;
}

function sourceLabel(item: ResumeMaterial) {
  if (item.sourceType === 'learning_task') return '学习任务沉淀';
  if (item.sourceTaskId) return '任务沉淀';
  return '手动录入';
}

function tagsFromText() {
  return material.tagsText
    .split(/[,，\s]+/)
    .map((item) => item.trim())
    .filter(Boolean);
}

function resetForm() {
  editingId.value = null;
  material.type = typeOptions[0].value;
  material.title = '';
  material.content = '';
  material.tagsText = '';
  optimizedPreview.value = '';
}

function handlePreviewMaterial() {
  if (!material.content.trim()) {
    ElMessage.warning('请先填写素材内容');
    return;
  }
  optimizedPreview.value = optimizeResumeMaterialMock(material.type, material.content);
}

function startEdit(item: ResumeMaterial) {
  editingId.value = item.id;
  material.type = typeOptions.find((option) => option.value === item.type || option.label === item.type)?.value || typeOptions[0].value;
  material.title = item.title;
  material.content = item.originalContent;
  material.tagsText = item.tags.join(' ');
  optimizedPreview.value = '';
}

async function loadMaterials() {
  try {
    loading.value = true;
    materials.value = await getResumeMaterialsApi();
    usingFallback.value = false;
  } catch {
    materials.value = resumeMaterials.map((item) => ({ ...item }));
    usingFallback.value = true;
    ElMessage.warning('后端暂不可用，已显示本地示例素材');
  } finally {
    loading.value = false;
  }
}

async function handleSubmitMaterial() {
  if (!material.content.trim()) {
    ElMessage.warning('请填写素材内容');
    return;
  }

  const payload = {
    type: material.type,
    title: material.title || `${typeLabel(material.type)}素材`,
    content: material.content,
    tags: tagsFromText(),
  };

  try {
    submitting.value = true;
    if (editingId.value) {
      const updated = await updateResumeMaterialApi(editingId.value, payload);
      materials.value = materials.value.map((item) => (item.id === updated.id ? updated : item));
      ElMessage.success('素材已更新');
    } else {
      const created = await createResumeMaterialApi(payload);
      materials.value.unshift(created);
      ElMessage.success(created.optimizationSource === 'llm' ? '素材已保存并完成 AI 优化' : '素材已保存');
    }
    usingFallback.value = false;
    resetForm();
  } catch {
    if (editingId.value) {
      materials.value = materials.value.map((item) =>
        item.id === editingId.value
          ? {
              ...item,
              type: payload.type,
              title: payload.title,
              originalContent: payload.content,
              tags: payload.tags,
            }
          : item,
      );
      ElMessage.warning('后端暂不可用，修改仅保留在当前页面');
    } else {
      materials.value.unshift(
        createResumeMaterialMock({
          type: payload.type,
          title: payload.title,
          content: payload.content,
          optimizedContent: optimizedPreview.value || optimizeResumeMaterialMock(payload.type, payload.content),
        }),
      );
      ElMessage.warning('后端暂不可用，素材已加入本地列表');
    }
    usingFallback.value = true;
    resetForm();
  } finally {
    submitting.value = false;
  }
}

async function handleOptimizeSaved(item: ResumeMaterial) {
  try {
    optimizingId.value = item.id;
    const optimized = await optimizeResumeMaterialApi(item.id);
    materials.value = materials.value.map((materialItem) =>
      materialItem.id === optimized.id ? optimized : materialItem,
    );
    usingFallback.value = false;
    ElMessage.success(optimized.optimizationSource === 'llm' ? 'AI 优化已完成' : '已使用本地规则优化');
  } catch {
    const optimizedContent = optimizeResumeMaterialMock(item.type, item.originalContent);
    materials.value = materials.value.map((materialItem) =>
      materialItem.id === item.id
        ? { ...materialItem, optimizedContent, optimizationSource: 'local_fallback' }
        : materialItem,
    );
    usingFallback.value = true;
    ElMessage.warning('后端暂不可用，已使用本地规则优化');
  } finally {
    optimizingId.value = null;
  }
}

async function handleDeleteMaterial(item: ResumeMaterial) {
  try {
    await ElMessageBox.confirm(`确认删除「${item.title}」吗？`, '删除素材', {
      confirmButtonText: '删除',
      cancelButtonText: '取消',
      type: 'warning',
    });
  } catch {
    return;
  }

  try {
    deletingId.value = item.id;
    await deleteResumeMaterialApi(item.id);
    materials.value = materials.value.filter((materialItem) => materialItem.id !== item.id);
    if (editingId.value === item.id) resetForm();
    ElMessage.success('素材已删除');
  } catch {
    materials.value = materials.value.filter((materialItem) => materialItem.id !== item.id);
    usingFallback.value = true;
    if (editingId.value === item.id) resetForm();
    ElMessage.warning('后端暂不可用，已从当前页面移除');
  } finally {
    deletingId.value = null;
  }
}

onMounted(loadMaterials);
</script>

<template>
  <section class="page-stack">
    <div class="module-hero">
      <div>
        <p class="eyebrow">Resume Materials</p>
        <h1>把成长成果沉淀成可复用素材</h1>
        <p class="page-intro">
          项目、学习任务、面试复盘和实习经历都会进入真实素材库，后续用于 JD 匹配、定制简历和模拟面试。
        </p>
      </div>
      <div class="module-hero__status">
        <span>素材数量</span>
        <strong>{{ materialCountText }}</strong>
        <em>{{ taskMaterialCount }} 条来自学习任务 · {{ usingFallback ? '本地示例' : '数据库同步' }}</em>
      </div>
    </div>

    <el-alert
      v-if="usingFallback"
      title="当前显示本地示例或临时页面数据，后端恢复后会重新读取数据库。"
      type="warning"
      show-icon
      :closable="false"
    />

    <div class="module-grid module-grid--side resume-material-layout">
      <el-card shadow="never" class="module-card material-panel" v-loading="loading">
        <template #header>
          <div class="section-header">
            <span>素材库</span>
            <el-button size="small" @click="loadMaterials">刷新</el-button>
          </div>
        </template>

        <div class="material-tags">
          <span v-for="stat in categoryStats" :key="stat.value" :class="{ active: stat.count > 0 }">
            {{ stat.label }} {{ stat.count }}
          </span>
        </div>

        <el-empty v-if="!materials.length && !loading" description="暂无素材" />

        <div v-else class="material-preview">
          <article v-for="item in materials" :key="item.id">
            <div class="material-item__header">
              <div>
                <strong>{{ item.title }}</strong>
                <div class="material-item__meta">
                  <el-tag size="small" type="info">{{ typeLabel(item.type) }}</el-tag>
                  <el-tag size="small" :type="item.sourceTaskId ? 'success' : 'info'">
                    {{ sourceLabel(item) }}
                  </el-tag>
                  <el-tag
                    v-if="item.optimizationSource"
                    size="small"
                    :type="item.optimizationSource === 'llm' ? 'success' : 'warning'"
                  >
                    {{ item.optimizationSource === 'llm' ? '大模型优化' : '本地优化' }}
                  </el-tag>
                  <small v-if="item.model">{{ item.model }}</small>
                </div>
              </div>
              <div class="material-item__actions">
                <el-button size="small" @click="startEdit(item)">编辑</el-button>
                <el-button
                  size="small"
                  type="primary"
                  :loading="optimizingId === item.id"
                  @click="handleOptimizeSaved(item)"
                >
                  优化
                </el-button>
                <el-button
                  size="small"
                  type="danger"
                  plain
                  :loading="deletingId === item.id"
                  @click="handleDeleteMaterial(item)"
                >
                  删除
                </el-button>
              </div>
            </div>
            <p>{{ item.optimizedContent || item.originalContent }}</p>
            <div v-if="item.tags.length" class="material-item__tags">
              <el-tag v-for="tag in item.tags" :key="`${item.id}-${tag}`" size="small">
                {{ tag }}
              </el-tag>
            </div>
          </article>
        </div>
      </el-card>

      <el-card shadow="never" class="module-card">
        <template #header>
          <div class="section-header">
            <span>{{ formTitle }}</span>
            <el-button v-if="editingId" size="small" @click="resetForm">取消编辑</el-button>
          </div>
        </template>

        <el-form :model="material" label-width="90px">
          <el-form-item label="素材类型">
            <el-select v-model="material.type">
              <el-option
                v-for="type in typeOptions"
                :key="type.value"
                :label="type.label"
                :value="type.value"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="素材标题">
            <el-input v-model="material.title" placeholder="例如：课程管理系统登录模块" />
          </el-form-item>
          <el-form-item label="素材标签">
            <el-input v-model="material.tagsText" placeholder="例如：Vue SpringBoot API" />
          </el-form-item>
          <el-form-item label="原始描述">
            <el-input
              v-model="material.content"
              type="textarea"
              :rows="7"
              placeholder="写清楚背景、你负责的内容、使用的技术或方法、最后产出。"
            />
          </el-form-item>

          <div v-if="optimizedPreview" class="mock-result">
            <span>本地预览</span>
            <p>{{ optimizedPreview }}</p>
          </div>

          <div class="form-actions">
            <el-button type="primary" :loading="submitting" @click="handleSubmitMaterial">
              {{ primaryActionText }}
            </el-button>
            <el-button @click="handlePreviewMaterial">本地预览</el-button>
          </div>
        </el-form>
      </el-card>
    </div>
  </section>
</template>

<style scoped>
.resume-material-layout {
  align-items: start;
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.material-preview {
  display: grid;
  gap: 14px;
}

.material-preview article {
  border: 1px solid var(--cp-border);
  border-radius: 8px;
  padding: 16px;
  background: #fff;
}

.material-item__header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 10px;
}

.material-item__meta,
.material-item__tags,
.material-item__actions,
.form-actions {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 8px;
}

.material-item__meta {
  margin-top: 8px;
}

.material-item__actions {
  justify-content: flex-end;
  min-width: 190px;
}

.material-preview p {
  margin: 0;
  color: var(--cp-text-secondary);
  line-height: 1.75;
  word-break: break-word;
}

.material-item__tags {
  margin-top: 12px;
}

.form-actions {
  padding-left: 90px;
}

@media (max-width: 768px) {
  .section-header,
  .material-item__header {
    align-items: flex-start;
    flex-direction: column;
  }

  .material-item__actions {
    justify-content: flex-start;
    min-width: 0;
  }

  .form-actions {
    padding-left: 0;
  }
}
</style>
