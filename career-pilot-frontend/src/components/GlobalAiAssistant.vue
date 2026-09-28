<script setup lang="ts">
import { nextTick, ref } from 'vue';
import { ElMessage } from 'element-plus';

import { sendAiChatApi } from '@/api/ai';
import { useCareerStore } from '@/stores/career';

interface ChatMessage {
  role: 'user' | 'assistant';
  content: string;
}

const careerStore = useCareerStore();
const visible = ref(false);
const loading = ref(false);
const input = ref('');
const messageListRef = ref<HTMLElement | null>(null);
const messages = ref<ChatMessage[]>([
  {
    role: 'assistant',
    content:
      '你好，我是 CareerPilot AI 助手。你可以告诉我年级、专业、目标岗位、技能基础或项目经历，我会帮你更新成长档案。',
  },
]);

function toggleAssistant() {
  visible.value = !visible.value;
}

async function scrollToBottom() {
  await nextTick();
  if (messageListRef.value) {
    messageListRef.value.scrollTop = messageListRef.value.scrollHeight;
  }
}

async function handleSend() {
  const content = input.value.trim();
  if (!content || loading.value) {
    return;
  }

  input.value = '';
  messages.value.push({ role: 'user', content });
  loading.value = true;
  await scrollToBottom();

  try {
    const response = await sendAiChatApi({ message: content });

    careerStore.applyAiChatResponse(response);
    messages.value.push({
      role: 'assistant',
      content: response.reply || '我已更新你的成长档案。',
    });
  } catch (error) {
    const message = error instanceof Error ? error.message : 'AI 助手暂时不可用';
    ElMessage.error(message);
    messages.value.push({
      role: 'assistant',
      content: 'AI 助手暂时不可用，请确认后端服务正常并重新登录后再试。',
    });
  } finally {
    loading.value = false;
    await scrollToBottom();
  }
}
</script>

<template>
  <div class="global-ai">
    <transition name="assistant-panel">
      <section v-if="visible" class="global-ai__panel" aria-label="CareerPilot AI 助手">
        <header class="global-ai__header">
          <div>
            <strong>CareerPilot AI</strong>
            <span>成长档案助手</span>
          </div>
          <button type="button" class="global-ai__close" @click="toggleAssistant">×</button>
        </header>

        <div ref="messageListRef" class="global-ai__messages">
          <article
            v-for="(message, index) in messages"
            :key="`${message.role}-${index}`"
            class="global-ai__message"
            :class="`is-${message.role}`"
          >
            {{ message.content }}
          </article>
          <article v-if="loading" class="global-ai__message is-assistant">正在分析并更新成长档案...</article>
        </div>

        <footer class="global-ai__footer">
          <el-input
            v-model="input"
            :disabled="loading"
            type="textarea"
            :rows="3"
            resize="none"
            placeholder="例如：我是大二软件工程专业，目标岗位是前端开发，目前会 Vue，但 TypeScript 比较弱。"
            @keydown.enter.exact.prevent="handleSend"
          />
          <el-button type="success" :loading="loading" @click="handleSend">发送</el-button>
        </footer>
      </section>
    </transition>

    <button type="button" class="global-ai__trigger" @click="toggleAssistant">
      <span>AI</span>
      <strong>助手</strong>
    </button>
  </div>
</template>

<style scoped>
.global-ai {
  position: fixed;
  right: 32px;
  bottom: 32px;
  z-index: 1000;
}

.global-ai__trigger {
  width: 72px;
  height: 72px;
  border: 0;
  border-radius: 24px;
  background: #16a34a;
  color: #ffffff;
  box-shadow: 0 14px 36px rgba(22, 163, 74, 0.28);
  cursor: pointer;
  display: grid;
  place-items: center;
  align-content: center;
  gap: 2px;
}

.global-ai__trigger span {
  font-size: 18px;
  font-weight: 800;
  line-height: 1;
}

.global-ai__trigger strong {
  font-size: 13px;
  font-weight: 700;
}

.global-ai__panel {
  position: absolute;
  right: 0;
  bottom: 88px;
  width: 420px;
  height: 560px;
  border: 1px solid #dcefe4;
  border-radius: 20px;
  background: #ffffff;
  box-shadow: 0 24px 70px rgba(15, 23, 42, 0.14);
  overflow: hidden;
  display: grid;
  grid-template-rows: auto 1fr auto;
}

.global-ai__header {
  padding: 18px 20px;
  border-bottom: 1px solid #e7f4ed;
  background: #f7fcf9;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.global-ai__header strong {
  display: block;
  color: #0f172a;
  font-size: 16px;
}

.global-ai__header span {
  color: #64748b;
  font-size: 13px;
}

.global-ai__close {
  width: 32px;
  height: 32px;
  border: 1px solid #dcefe4;
  border-radius: 10px;
  background: #ffffff;
  color: #475569;
  cursor: pointer;
  font-size: 20px;
  line-height: 1;
}

.global-ai__messages {
  padding: 18px;
  overflow-y: auto;
  background: #fbfefc;
}

.global-ai__message {
  width: fit-content;
  max-width: 86%;
  margin-bottom: 12px;
  padding: 11px 13px;
  border-radius: 14px;
  color: #0f172a;
  font-size: 14px;
  line-height: 1.65;
  white-space: pre-wrap;
}

.global-ai__message.is-assistant {
  border: 1px solid #dcefe4;
  background: #ffffff;
}

.global-ai__message.is-user {
  margin-left: auto;
  background: #16a34a;
  color: #ffffff;
}

.global-ai__footer {
  padding: 14px;
  border-top: 1px solid #e7f4ed;
  background: #ffffff;
  display: grid;
  grid-template-columns: 1fr auto;
  gap: 10px;
  align-items: end;
}

.assistant-panel-enter-active,
.assistant-panel-leave-active {
  transition: opacity 0.18s ease, transform 0.18s ease;
}

.assistant-panel-enter-from,
.assistant-panel-leave-to {
  opacity: 0;
  transform: translateY(8px);
}

@media (max-width: 768px) {
  .global-ai {
    right: 18px;
    bottom: 18px;
  }

  .global-ai__panel {
    width: calc(100vw - 36px);
    height: min(560px, calc(100vh - 126px));
  }
}
</style>
