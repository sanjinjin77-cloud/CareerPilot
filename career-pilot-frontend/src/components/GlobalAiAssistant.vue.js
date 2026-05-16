import { nextTick, ref } from 'vue';
import { ElMessage } from 'element-plus';
import { sendAiChatApi } from '@/api/ai';
import { useCareerStore } from '@/stores/career';
const careerStore = useCareerStore();
const visible = ref(false);
const loading = ref(false);
const input = ref('');
const messageListRef = ref(null);
const messages = ref([
    {
        role: 'assistant',
        content: '你好，我是 CareerPilot AI 助手。你可以告诉我年级、专业、目标岗位、技能基础或项目经历，我会帮你更新成长档案。',
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
        const response = await sendAiChatApi({
            userId: 1,
            message: content,
        });
        careerStore.applyAiChatResponse(response);
        messages.value.push({
            role: 'assistant',
            content: response.reply || '我已更新你的成长档案。',
        });
    }
    catch (error) {
        const message = error instanceof Error ? error.message : 'AI 助手暂时不可用';
        ElMessage.error(message);
        messages.value.push({
            role: 'assistant',
            content: '后端服务暂时不可用，请确认 Java 后端已在 8080 端口启动后再试。',
        });
    }
    finally {
        loading.value = false;
        await scrollToBottom();
    }
}
debugger; /* PartiallyEnd: #3632/scriptSetup.vue */
const __VLS_ctx = {};
let __VLS_components;
let __VLS_directives;
/** @type {__VLS_StyleScopedClasses['global-ai__trigger']} */ ;
/** @type {__VLS_StyleScopedClasses['global-ai__trigger']} */ ;
/** @type {__VLS_StyleScopedClasses['global-ai__header']} */ ;
/** @type {__VLS_StyleScopedClasses['global-ai__header']} */ ;
/** @type {__VLS_StyleScopedClasses['global-ai__message']} */ ;
/** @type {__VLS_StyleScopedClasses['global-ai__message']} */ ;
/** @type {__VLS_StyleScopedClasses['global-ai']} */ ;
/** @type {__VLS_StyleScopedClasses['global-ai__panel']} */ ;
// CSS variable injection 
// CSS variable injection end 
__VLS_asFunctionalElement(__VLS_intrinsicElements.div, __VLS_intrinsicElements.div)({
    ...{ class: "global-ai" },
});
const __VLS_0 = {}.transition;
/** @type {[typeof __VLS_components.Transition, typeof __VLS_components.transition, typeof __VLS_components.Transition, typeof __VLS_components.transition, ]} */ ;
// @ts-ignore
const __VLS_1 = __VLS_asFunctionalComponent(__VLS_0, new __VLS_0({
    name: "assistant-panel",
}));
const __VLS_2 = __VLS_1({
    name: "assistant-panel",
}, ...__VLS_functionalComponentArgsRest(__VLS_1));
__VLS_3.slots.default;
if (__VLS_ctx.visible) {
    __VLS_asFunctionalElement(__VLS_intrinsicElements.section, __VLS_intrinsicElements.section)({
        ...{ class: "global-ai__panel" },
        'aria-label': "CareerPilot AI 助手",
    });
    __VLS_asFunctionalElement(__VLS_intrinsicElements.header, __VLS_intrinsicElements.header)({
        ...{ class: "global-ai__header" },
    });
    __VLS_asFunctionalElement(__VLS_intrinsicElements.div, __VLS_intrinsicElements.div)({});
    __VLS_asFunctionalElement(__VLS_intrinsicElements.strong, __VLS_intrinsicElements.strong)({});
    __VLS_asFunctionalElement(__VLS_intrinsicElements.span, __VLS_intrinsicElements.span)({});
    __VLS_asFunctionalElement(__VLS_intrinsicElements.button, __VLS_intrinsicElements.button)({
        ...{ onClick: (__VLS_ctx.toggleAssistant) },
        type: "button",
        ...{ class: "global-ai__close" },
    });
    __VLS_asFunctionalElement(__VLS_intrinsicElements.div, __VLS_intrinsicElements.div)({
        ref: "messageListRef",
        ...{ class: "global-ai__messages" },
    });
    /** @type {typeof __VLS_ctx.messageListRef} */ ;
    for (const [message, index] of __VLS_getVForSourceType((__VLS_ctx.messages))) {
        __VLS_asFunctionalElement(__VLS_intrinsicElements.article, __VLS_intrinsicElements.article)({
            key: (`${message.role}-${index}`),
            ...{ class: "global-ai__message" },
            ...{ class: (`is-${message.role}`) },
        });
        (message.content);
    }
    if (__VLS_ctx.loading) {
        __VLS_asFunctionalElement(__VLS_intrinsicElements.article, __VLS_intrinsicElements.article)({
            ...{ class: "global-ai__message is-assistant" },
        });
    }
    __VLS_asFunctionalElement(__VLS_intrinsicElements.footer, __VLS_intrinsicElements.footer)({
        ...{ class: "global-ai__footer" },
    });
    const __VLS_4 = {}.ElInput;
    /** @type {[typeof __VLS_components.ElInput, typeof __VLS_components.elInput, ]} */ ;
    // @ts-ignore
    const __VLS_5 = __VLS_asFunctionalComponent(__VLS_4, new __VLS_4({
        ...{ 'onKeydown': {} },
        modelValue: (__VLS_ctx.input),
        disabled: (__VLS_ctx.loading),
        type: "textarea",
        rows: (3),
        resize: "none",
        placeholder: "例如：我是大二软件工程专业，目标岗位是前端开发，目前会 Vue，但是 TypeScript 比较弱。",
    }));
    const __VLS_6 = __VLS_5({
        ...{ 'onKeydown': {} },
        modelValue: (__VLS_ctx.input),
        disabled: (__VLS_ctx.loading),
        type: "textarea",
        rows: (3),
        resize: "none",
        placeholder: "例如：我是大二软件工程专业，目标岗位是前端开发，目前会 Vue，但是 TypeScript 比较弱。",
    }, ...__VLS_functionalComponentArgsRest(__VLS_5));
    let __VLS_8;
    let __VLS_9;
    let __VLS_10;
    const __VLS_11 = {
        onKeydown: (__VLS_ctx.handleSend)
    };
    var __VLS_7;
    const __VLS_12 = {}.ElButton;
    /** @type {[typeof __VLS_components.ElButton, typeof __VLS_components.elButton, typeof __VLS_components.ElButton, typeof __VLS_components.elButton, ]} */ ;
    // @ts-ignore
    const __VLS_13 = __VLS_asFunctionalComponent(__VLS_12, new __VLS_12({
        ...{ 'onClick': {} },
        type: "success",
        loading: (__VLS_ctx.loading),
    }));
    const __VLS_14 = __VLS_13({
        ...{ 'onClick': {} },
        type: "success",
        loading: (__VLS_ctx.loading),
    }, ...__VLS_functionalComponentArgsRest(__VLS_13));
    let __VLS_16;
    let __VLS_17;
    let __VLS_18;
    const __VLS_19 = {
        onClick: (__VLS_ctx.handleSend)
    };
    __VLS_15.slots.default;
    var __VLS_15;
}
var __VLS_3;
__VLS_asFunctionalElement(__VLS_intrinsicElements.button, __VLS_intrinsicElements.button)({
    ...{ onClick: (__VLS_ctx.toggleAssistant) },
    type: "button",
    ...{ class: "global-ai__trigger" },
});
__VLS_asFunctionalElement(__VLS_intrinsicElements.span, __VLS_intrinsicElements.span)({});
__VLS_asFunctionalElement(__VLS_intrinsicElements.strong, __VLS_intrinsicElements.strong)({});
/** @type {__VLS_StyleScopedClasses['global-ai']} */ ;
/** @type {__VLS_StyleScopedClasses['global-ai__panel']} */ ;
/** @type {__VLS_StyleScopedClasses['global-ai__header']} */ ;
/** @type {__VLS_StyleScopedClasses['global-ai__close']} */ ;
/** @type {__VLS_StyleScopedClasses['global-ai__messages']} */ ;
/** @type {__VLS_StyleScopedClasses['global-ai__message']} */ ;
/** @type {__VLS_StyleScopedClasses['global-ai__message']} */ ;
/** @type {__VLS_StyleScopedClasses['is-assistant']} */ ;
/** @type {__VLS_StyleScopedClasses['global-ai__footer']} */ ;
/** @type {__VLS_StyleScopedClasses['global-ai__trigger']} */ ;
var __VLS_dollars;
const __VLS_self = (await import('vue')).defineComponent({
    setup() {
        return {
            visible: visible,
            loading: loading,
            input: input,
            messageListRef: messageListRef,
            messages: messages,
            toggleAssistant: toggleAssistant,
            handleSend: handleSend,
        };
    },
});
export default (await import('vue')).defineComponent({
    setup() {
        return {};
    },
});
; /* PartiallyEnd: #4569/main.vue */
