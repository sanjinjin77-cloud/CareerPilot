<script setup lang="ts">
import { computed, reactive, ref } from 'vue';
import { ElMessage } from 'element-plus';

import { createResumeMaterialApi, getResumeMaterialsApi } from '@/api/career';
import { resumeMaterials } from '@/mock/careerPilotMock';
import { createResumeMaterialMock, optimizeResumeMaterialMock } from '@/services/mockCareerService';
import type { ResumeMaterial } from '@/types';

const materials = ref<ResumeMaterial[]>(resumeMaterials.map((item) => ({ ...item })));

const material = reactive({
  type: resumeMaterials[0].type,
  title: '',
  content: resumeMaterials[0].originalContent,
});

const optimizedPreview = ref('');
const loading = ref(false);
const materialCountText = computed(() => `${materials.value.length} 条`);

function handleOptimizeMaterial() {
  if (!material.content.trim()) {
    ElMessage.warning('请先输入原始描述');
    return;
  }

  optimizedPreview.value = optimizeResumeMaterialMock(material.type, material.content);
  ElMessage.success('已生成 mock 优化表达');
}

async function handleAddMaterial() {
  if (!material.content.trim()) {
    ElMessage.warning('请填写素材内容');
    return;
  }

  let nextMaterial: ResumeMaterial;

  try {
    loading.value = true;
    nextMaterial = await createResumeMaterialApi({
      type: material.type,
      title: material.title || `${material.type}素材`,
      content: material.content,
      tags: [material.type, '待完善'],
    });
    ElMessage.success('素材已保存到后端');
  } catch {
    nextMaterial = createResumeMaterialMock({
      type: material.type,
      title: material.title || `${material.type}素材`,
      content: material.content,
      optimizedContent: optimizedPreview.value,
    });
    ElMessage.warning('后端不可用，素材已加入本地 mock 列表');
  } finally {
    loading.value = false;
  }

  materials.value.unshift(nextMaterial);
  material.title = '';
  material.content = '';
  optimizedPreview.value = '';
}

getResumeMaterialsApi()
  .then((items) => {
    if (items.length) materials.value = items;
  })
  .catch(() => {
    // 后端不可用时保留 mock 素材。
  });
</script>

<template>
  <section class="page-stack">
    <div class="module-hero">
      <div>
        <p class="eyebrow">Resume Materials</p>
        <h1>把大学经历沉淀成可复用素材</h1>
        <p class="page-intro">
          项目、比赛、课程设计、实习和技能经历都可以进入素材库，后续用于 JD 匹配和简历优化。
        </p>
      </div>
      <div class="module-hero__status">
        <span>素材数量</span>
        <strong>{{ materialCountText }}</strong>
        <em>简历完善度 58%</em>
      </div>
    </div>

    <div class="module-grid module-grid--side">
      <el-card shadow="never" class="module-card material-panel">
        <template #header>素材分类</template>
        <div class="material-tags">
          <span class="active">项目经历 4</span>
          <span>比赛经历 2</span>
          <span>课程设计 3</span>
          <span>实习经历 1</span>
          <span>技能经历 2</span>
        </div>
        <div class="material-preview">
          <article v-for="item in materials" :key="item.id">
            <strong>{{ item.title }}</strong>
            <p>{{ item.optimizedContent }}</p>
          </article>
        </div>
      </el-card>

      <el-card shadow="never" class="module-card">
        <template #header>新增素材</template>
      <el-form :model="material" label-width="90px">
        <el-form-item label="素材类型">
          <el-select v-model="material.type">
            <el-option label="项目经历" value="项目经历" />
            <el-option label="比赛经历" value="比赛经历" />
            <el-option label="课程设计" value="课程设计" />
            <el-option label="实习经历" value="实习经历" />
            <el-option label="技能经历" value="技能经历" />
          </el-select>
        </el-form-item>
        <el-form-item label="素材标题">
          <el-input v-model="material.title" placeholder="例如：课程管理系统登录模块" />
        </el-form-item>
        <el-form-item label="原始描述">
          <el-input v-model="material.content" type="textarea" :rows="5" />
        </el-form-item>
        <div v-if="optimizedPreview" class="mock-result">
          <span>AI 优化预览</span>
          <p>{{ optimizedPreview }}</p>
        </div>
        <el-button type="primary" :loading="loading" @click="handleAddMaterial">添加素材</el-button>
        <el-button @click="handleOptimizeMaterial">AI 优化表达</el-button>
      </el-form>
      </el-card>
    </div>
  </section>
</template>
