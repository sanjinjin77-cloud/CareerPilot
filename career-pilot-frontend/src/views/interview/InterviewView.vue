<script setup lang="ts">
import { computed, reactive, ref } from 'vue';
import { ElMessage } from 'element-plus';

import { interviewRecord } from '@/mock/careerPilotMock';
import { interviewQuestions, scoreInterviewAnswerMock } from '@/services/mockCareerService';

const answer = ref(interviewRecord.answer);
const currentQuestionIndex = ref(0);
const feedback = ref(interviewRecord.feedback);
const scores = reactive({ ...interviewRecord.scores });
const round = ref(2);
const currentQuestion = computed(() => interviewQuestions[currentQuestionIndex.value]);

function handleSubmitAnswer() {
  if (!answer.value.trim()) {
    ElMessage.warning('请先输入面试回答');
    return;
  }

  const result = scoreInterviewAnswerMock(answer.value);

  scores.technical = result.scores.technical;
  scores.project = result.scores.project;
  scores.logic = result.scores.logic;
  scores.star = result.scores.star;
  feedback.value = result.feedback;
  ElMessage.success('已生成 mock 面试反馈');
}

function handleNextQuestion() {
  currentQuestionIndex.value = (currentQuestionIndex.value + 1) % interviewQuestions.length;
  round.value += 1;
  answer.value = '';
  feedback.value = '新问题已生成，请结合项目经历作答。';
}
</script>

<template>
  <section class="page-stack">
    <div class="module-hero">
      <div>
        <p class="eyebrow">Mock Interview</p>
        <h1>围绕目标岗位进行真实追问训练</h1>
        <p class="page-intro">
          根据目标岗位、JD 和简历素材生成问题，并从技术能力、项目理解、表达逻辑和 STAR 法则评分。
        </p>
      </div>
      <div class="module-hero__status">
        <span>训练轮次</span>
        <strong>第 {{ round }} 轮</strong>
        <em>{{ feedback }}</em>
      </div>
    </div>

    <div class="module-grid module-grid--side">
      <el-card shadow="never" class="module-card interview-card">
        <template #header>当前问题</template>
        <p class="question">
          {{ currentQuestion }}
        </p>
        <el-input
          v-model="answer"
          type="textarea"
          :rows="7"
          placeholder="请输入你的回答，后续将接入 AI 评分与反馈"
        />
        <div class="action-row">
          <el-button type="primary" @click="handleSubmitAnswer">提交回答</el-button>
          <el-button @click="handleNextQuestion">换一道问题</el-button>
        </div>
      </el-card>

      <el-card shadow="never" class="module-card">
        <template #header>评分维度</template>
        <div class="score-list">
          <span>技术能力 <strong>{{ scores.technical }}%</strong></span>
          <span>项目理解 <strong>{{ scores.project }}%</strong></span>
          <span>表达逻辑 <strong>{{ scores.logic }}%</strong></span>
          <span>STAR 法则 <strong>{{ scores.star }}%</strong></span>
        </div>
        <div class="mock-result interview-feedback">
          <span>AI 反馈</span>
          <p>{{ feedback }}</p>
        </div>
      </el-card>
    </div>
  </section>
</template>
