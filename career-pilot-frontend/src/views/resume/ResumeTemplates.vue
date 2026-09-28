<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage, ElMessageBox } from 'element-plus';

import {
  deleteResumeDocumentApi,
  getResumeDocumentsApi,
  updateResumeDocumentApi,
} from '@/api/career';
import type { ResumeDocument } from '@/types';

const router = useRouter();

const documents = ref<ResumeDocument[]>([]);
const selectedId = ref<number | null>(null);
const loading = ref(false);
const saving = ref(false);
const deleting = ref(false);
const finalizing = ref(false);
const backendUnavailable = ref(false);

const editor = reactive({
  title: '',
  content: '',
  optimizedContent: '',
  status: 'draft',
});

const selectedDocument = computed(
  () => documents.value.find((document) => document.id === selectedId.value) || null,
);
const documentCountText = computed(() => `${documents.value.length} 份`);
const finalDocument = computed(() => documents.value.find((document) => document.status === 'final') || null);

function sourceLabel(sourceType: string) {
  const labels: Record<string, string> = {
    jd_customized: 'JD 定制',
    uploaded: '上传简历',
    manual: '手动创建',
  };
  return labels[sourceType] || sourceType || '简历草稿';
}

function statusLabel(status: string) {
  const labels: Record<string, string> = {
    draft: '草稿',
    final: '最终版',
    archived: '已归档',
  };
  return labels[status] || status || '草稿';
}

function statusTagType(status: string) {
  if (status === 'final') return 'success';
  if (status === 'archived') return 'info';
  return 'warning';
}

function formatTime(value?: string) {
  if (!value) return '暂无时间';
  return value.replace('T', ' ').slice(0, 16);
}

function syncEditor(document: ResumeDocument | null) {
  editor.title = document?.title || '';
  editor.content = document?.content || '';
  editor.optimizedContent = document?.optimizedContent || '';
  editor.status = document?.status || 'draft';
}

function selectDocument(document: ResumeDocument) {
  selectedId.value = document.id;
  syncEditor(document);
}

async function loadDocuments() {
  try {
    loading.value = true;
    const items = await getResumeDocumentsApi();
    documents.value = items;
    backendUnavailable.value = false;
    if (items.length) {
      const current = selectedId.value ? items.find((item) => item.id === selectedId.value) : null;
      selectDocument(current || items[0]);
    } else {
      selectedId.value = null;
      syncEditor(null);
    }
  } catch {
    backendUnavailable.value = true;
    ElMessage.warning('后端暂不可用，无法同步简历草稿');
  } finally {
    loading.value = false;
  }
}

async function persistDocument(status = editor.status) {
  if (!selectedDocument.value) {
    ElMessage.warning('请先选择一份简历草稿');
    return null;
  }
  if (!editor.title.trim() || !editor.optimizedContent.trim()) {
    ElMessage.warning('标题和优化版本不能为空');
    return null;
  }

  const updated = await updateResumeDocumentApi(selectedDocument.value.id, {
    title: editor.title,
    content: editor.content,
    optimizedContent: editor.optimizedContent,
    status,
  });
  documents.value = documents.value.map((item) => {
    if (status === 'final' && item.id !== updated.id && item.status === 'final') {
      return { ...item, status: 'draft' };
    }
    return item.id === updated.id ? updated : item;
  });
  selectDocument(updated);
  backendUnavailable.value = false;
  return updated;
}

async function saveDocument() {
  try {
    saving.value = true;
    const updated = await persistDocument();
    if (updated) ElMessage.success('简历草稿已保存');
  } catch {
    backendUnavailable.value = true;
    ElMessage.warning('保存失败，请确认后端和数据库是否可用');
  } finally {
    saving.value = false;
  }
}

async function setAsFinal() {
  try {
    finalizing.value = true;
    const updated = await persistDocument('final');
    if (updated) ElMessage.success('已设为最终版，模拟面试会优先使用这份简历');
  } catch {
    backendUnavailable.value = true;
    ElMessage.warning('设为最终版失败，请确认后端和数据库是否可用');
  } finally {
    finalizing.value = false;
  }
}

async function copyOptimizedContent() {
  if (!editor.optimizedContent.trim()) {
    ElMessage.warning('暂无可复制内容');
    return;
  }
  try {
    await navigator.clipboard.writeText(editor.optimizedContent);
    ElMessage.success('已复制优化版简历内容');
  } catch {
    ElMessage.warning('复制失败，可以手动选中文本复制');
  }
}

async function deleteDocument() {
  if (!selectedDocument.value) {
    ElMessage.warning('请先选择一份简历草稿');
    return;
  }

  const document = selectedDocument.value;
  try {
    await ElMessageBox.confirm(`确认删除「${document.title}」吗？`, '删除简历草稿', {
      confirmButtonText: '删除',
      cancelButtonText: '取消',
      type: 'warning',
    });
  } catch {
    return;
  }

  try {
    deleting.value = true;
    await deleteResumeDocumentApi(document.id);
    documents.value = documents.value.filter((item) => item.id !== document.id);
    const next = documents.value[0] || null;
    selectedId.value = next?.id || null;
    syncEditor(next);
    backendUnavailable.value = false;
    ElMessage.success('简历草稿已删除');
  } catch {
    backendUnavailable.value = true;
    ElMessage.warning('删除失败，请确认后端和数据库是否可用');
  } finally {
    deleting.value = false;
  }
}

onMounted(loadDocuments);
</script>

<template>
  <main class="page-stack">
    <section class="module-hero">
      <div>
        <p class="eyebrow">Resume Builder</p>
        <h1>简历草稿管理</h1>
        <p class="page-intro">
          管理由 JD 分析生成的定制简历，编辑后可设为最终版，并作为后续模拟面试的简历依据。
        </p>
      </div>
      <div class="module-hero__status">
        <span>最终版简历</span>
        <strong>{{ finalDocument?.title || '尚未定稿' }}</strong>
        <em>{{ documentCountText }} · {{ backendUnavailable ? '同步异常' : '数据库同步' }}</em>
      </div>
    </section>

    <el-alert
      v-if="backendUnavailable"
      title="当前未能连接后端，简历草稿暂时无法同步。"
      type="warning"
      show-icon
      :closable="false"
    />

    <div class="module-grid module-grid--side resume-document-layout">
      <el-card shadow="never" class="module-card document-list" v-loading="loading">
        <template #header>
          <div class="section-header">
            <span>历史草稿</span>
            <el-button size="small" @click="loadDocuments">刷新</el-button>
          </div>
        </template>

        <el-empty v-if="!documents.length && !loading" description="暂无简历草稿">
          <el-button type="primary" @click="router.push('/career/jd-analysis')">去 JD 分析生成</el-button>
        </el-empty>

        <div v-else class="document-list__items">
          <button
            v-for="document in documents"
            :key="document.id"
            class="document-list__item"
            :class="{ active: document.id === selectedId }"
            type="button"
            @click="selectDocument(document)"
          >
            <span>{{ document.title }}</span>
            <small>{{ sourceLabel(document.sourceType) }} · {{ statusLabel(document.status) }}</small>
            <em>{{ formatTime(document.updatedAt || document.createdAt) }}</em>
          </button>
        </div>
      </el-card>

      <el-card shadow="never" class="module-card document-editor">
        <template #header>
          <div class="section-header">
            <span>草稿编辑</span>
            <div class="header-actions">
              <el-button size="small" @click="copyOptimizedContent">复制</el-button>
              <el-button size="small" type="danger" plain :loading="deleting" @click="deleteDocument">
                删除
              </el-button>
            </div>
          </div>
        </template>

        <el-empty v-if="!selectedDocument" description="请选择或生成一份简历草稿" />

        <el-form v-else :model="editor" label-width="92px">
          <el-form-item label="草稿标题">
            <el-input v-model="editor.title" />
          </el-form-item>
          <el-form-item label="草稿状态">
            <el-select v-model="editor.status">
              <el-option label="草稿" value="draft" />
              <el-option label="最终版" value="final" />
              <el-option label="已归档" value="archived" />
            </el-select>
          </el-form-item>
          <el-form-item label="原始整理">
            <el-input v-model="editor.content" type="textarea" :rows="8" />
          </el-form-item>
          <el-form-item label="优化版本">
            <el-input v-model="editor.optimizedContent" type="textarea" :rows="12" />
          </el-form-item>

          <div class="document-meta">
            <el-tag type="info">{{ sourceLabel(selectedDocument.sourceType) }}</el-tag>
            <el-tag :type="statusTagType(selectedDocument.status)">
              {{ statusLabel(selectedDocument.status) }}
            </el-tag>
            <span>更新于 {{ formatTime(selectedDocument.updatedAt || selectedDocument.createdAt) }}</span>
          </div>

          <div class="form-actions">
            <el-button type="primary" :loading="saving" @click="saveDocument">保存草稿</el-button>
            <el-button type="success" plain :loading="finalizing" @click="setAsFinal">设为最终版</el-button>
            <el-button @click="router.push('/interview')">进入模拟面试</el-button>
            <el-button @click="router.push('/career/jd-analysis')">继续生成定制简历</el-button>
          </div>
        </el-form>
      </el-card>
    </div>
  </main>
</template>

<style scoped>
.resume-document-layout {
  align-items: start;
}

.section-header,
.header-actions,
.document-meta,
.form-actions {
  display: flex;
  align-items: center;
  gap: 10px;
}

.section-header {
  justify-content: space-between;
}

.header-actions,
.document-meta,
.form-actions {
  flex-wrap: wrap;
}

.document-list__items {
  display: grid;
  gap: 10px;
}

.document-list__item {
  display: grid;
  gap: 6px;
  width: 100%;
  padding: 14px;
  border: 1px solid var(--cp-border);
  border-radius: 8px;
  background: #fff;
  color: inherit;
  text-align: left;
  cursor: pointer;
}

.document-list__item.active {
  border-color: var(--cp-primary);
  background: rgba(64, 111, 255, 0.08);
}

.document-list__item span {
  font-weight: 700;
  word-break: break-word;
}

.document-list__item small,
.document-list__item em,
.document-meta span {
  color: var(--cp-text-secondary);
  font-style: normal;
}

.document-meta,
.form-actions {
  padding-left: 92px;
  margin-bottom: 16px;
}

@media (max-width: 768px) {
  .section-header {
    align-items: flex-start;
    flex-direction: column;
  }

  .document-meta,
  .form-actions {
    padding-left: 0;
  }
}
</style>
