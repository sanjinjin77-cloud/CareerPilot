import { computed, reactive, ref } from 'vue';
import { ElMessage } from 'element-plus';
import { jdAnalysis } from '@/mock/careerPilotMock';
import { analyzeJDMock, appendJDOptimizationSuggestionMock } from '@/services/mockCareerService';
const jdContent = ref(jdAnalysis.jdContent);
const analysis = reactive({
    keywords: [...jdAnalysis.keywords],
    recommendedMaterials: [...jdAnalysis.recommendedMaterials],
    suggestions: [...jdAnalysis.suggestions],
    matchScore: jdAnalysis.matchScore,
});
const hasAnalyzed = ref(false);
const scoreText = computed(() => `${analysis.matchScore}%`);
function handleAnalyzeJD() {
    if (!jdContent.value.trim()) {
        ElMessage.warning('请先粘贴岗位 JD');
        return;
    }
    const result = analyzeJDMock(jdContent.value);
    analysis.keywords = result.keywords;
    analysis.matchScore = result.matchScore;
    analysis.recommendedMaterials = result.recommendedMaterials;
    analysis.suggestions = result.suggestions;
    hasAnalyzed.value = true;
    ElMessage.success('已完成 mock JD 分析');
}
function handleGenerateSuggestion() {
    if (!hasAnalyzed.value) {
        handleAnalyzeJD();
        return;
    }
    analysis.suggestions = appendJDOptimizationSuggestionMock(analysis.suggestions);
    ElMessage.success('已生成 mock 简历优化建议');
}
debugger; /* PartiallyEnd: #3632/scriptSetup.vue */
const __VLS_ctx = {};
let __VLS_components;
let __VLS_directives;
__VLS_asFunctionalElement(__VLS_intrinsicElements.section, __VLS_intrinsicElements.section)({
    ...{ class: "page-stack" },
});
__VLS_asFunctionalElement(__VLS_intrinsicElements.div, __VLS_intrinsicElements.div)({
    ...{ class: "module-hero" },
});
__VLS_asFunctionalElement(__VLS_intrinsicElements.div, __VLS_intrinsicElements.div)({});
__VLS_asFunctionalElement(__VLS_intrinsicElements.p, __VLS_intrinsicElements.p)({
    ...{ class: "eyebrow" },
});
__VLS_asFunctionalElement(__VLS_intrinsicElements.h1, __VLS_intrinsicElements.h1)({});
__VLS_asFunctionalElement(__VLS_intrinsicElements.p, __VLS_intrinsicElements.p)({
    ...{ class: "page-intro" },
});
__VLS_asFunctionalElement(__VLS_intrinsicElements.div, __VLS_intrinsicElements.div)({
    ...{ class: "module-hero__status" },
});
__VLS_asFunctionalElement(__VLS_intrinsicElements.span, __VLS_intrinsicElements.span)({});
__VLS_asFunctionalElement(__VLS_intrinsicElements.strong, __VLS_intrinsicElements.strong)({});
(__VLS_ctx.scoreText);
__VLS_asFunctionalElement(__VLS_intrinsicElements.em, __VLS_intrinsicElements.em)({});
(__VLS_ctx.analysis.suggestions[0]);
__VLS_asFunctionalElement(__VLS_intrinsicElements.div, __VLS_intrinsicElements.div)({
    ...{ class: "module-grid module-grid--side" },
});
const __VLS_0 = {}.ElCard;
/** @type {[typeof __VLS_components.ElCard, typeof __VLS_components.elCard, typeof __VLS_components.ElCard, typeof __VLS_components.elCard, ]} */ ;
// @ts-ignore
const __VLS_1 = __VLS_asFunctionalComponent(__VLS_0, new __VLS_0({
    shadow: "never",
    ...{ class: "module-card jd-input-card" },
}));
const __VLS_2 = __VLS_1({
    shadow: "never",
    ...{ class: "module-card jd-input-card" },
}, ...__VLS_functionalComponentArgsRest(__VLS_1));
__VLS_3.slots.default;
{
    const { header: __VLS_thisSlot } = __VLS_3.slots;
}
const __VLS_4 = {}.ElInput;
/** @type {[typeof __VLS_components.ElInput, typeof __VLS_components.elInput, ]} */ ;
// @ts-ignore
const __VLS_5 = __VLS_asFunctionalComponent(__VLS_4, new __VLS_4({
    modelValue: (__VLS_ctx.jdContent),
    type: "textarea",
    rows: (10),
}));
const __VLS_6 = __VLS_5({
    modelValue: (__VLS_ctx.jdContent),
    type: "textarea",
    rows: (10),
}, ...__VLS_functionalComponentArgsRest(__VLS_5));
__VLS_asFunctionalElement(__VLS_intrinsicElements.div, __VLS_intrinsicElements.div)({
    ...{ class: "action-row" },
});
const __VLS_8 = {}.ElButton;
/** @type {[typeof __VLS_components.ElButton, typeof __VLS_components.elButton, typeof __VLS_components.ElButton, typeof __VLS_components.elButton, ]} */ ;
// @ts-ignore
const __VLS_9 = __VLS_asFunctionalComponent(__VLS_8, new __VLS_8({
    ...{ 'onClick': {} },
    type: "primary",
}));
const __VLS_10 = __VLS_9({
    ...{ 'onClick': {} },
    type: "primary",
}, ...__VLS_functionalComponentArgsRest(__VLS_9));
let __VLS_12;
let __VLS_13;
let __VLS_14;
const __VLS_15 = {
    onClick: (__VLS_ctx.handleAnalyzeJD)
};
__VLS_11.slots.default;
var __VLS_11;
const __VLS_16 = {}.ElButton;
/** @type {[typeof __VLS_components.ElButton, typeof __VLS_components.elButton, typeof __VLS_components.ElButton, typeof __VLS_components.elButton, ]} */ ;
// @ts-ignore
const __VLS_17 = __VLS_asFunctionalComponent(__VLS_16, new __VLS_16({
    ...{ 'onClick': {} },
}));
const __VLS_18 = __VLS_17({
    ...{ 'onClick': {} },
}, ...__VLS_functionalComponentArgsRest(__VLS_17));
let __VLS_20;
let __VLS_21;
let __VLS_22;
const __VLS_23 = {
    onClick: (__VLS_ctx.handleGenerateSuggestion)
};
__VLS_19.slots.default;
var __VLS_19;
var __VLS_3;
const __VLS_24 = {}.ElCard;
/** @type {[typeof __VLS_components.ElCard, typeof __VLS_components.elCard, typeof __VLS_components.ElCard, typeof __VLS_components.elCard, ]} */ ;
// @ts-ignore
const __VLS_25 = __VLS_asFunctionalComponent(__VLS_24, new __VLS_24({
    shadow: "never",
    ...{ class: "module-card" },
}));
const __VLS_26 = __VLS_25({
    shadow: "never",
    ...{ class: "module-card" },
}, ...__VLS_functionalComponentArgsRest(__VLS_25));
__VLS_27.slots.default;
{
    const { header: __VLS_thisSlot } = __VLS_27.slots;
}
__VLS_asFunctionalElement(__VLS_intrinsicElements.div, __VLS_intrinsicElements.div)({
    ...{ class: "keyword-cloud" },
});
for (const [keyword] of __VLS_getVForSourceType((__VLS_ctx.analysis.keywords))) {
    const __VLS_28 = {}.ElTag;
    /** @type {[typeof __VLS_components.ElTag, typeof __VLS_components.elTag, typeof __VLS_components.ElTag, typeof __VLS_components.elTag, ]} */ ;
    // @ts-ignore
    const __VLS_29 = __VLS_asFunctionalComponent(__VLS_28, new __VLS_28({
        key: (keyword),
    }));
    const __VLS_30 = __VLS_29({
        key: (keyword),
    }, ...__VLS_functionalComponentArgsRest(__VLS_29));
    __VLS_31.slots.default;
    (keyword);
    var __VLS_31;
}
__VLS_asFunctionalElement(__VLS_intrinsicElements.div, __VLS_intrinsicElements.div)({
    ...{ class: "match-preview" },
});
__VLS_asFunctionalElement(__VLS_intrinsicElements.span, __VLS_intrinsicElements.span)({});
__VLS_asFunctionalElement(__VLS_intrinsicElements.strong, __VLS_intrinsicElements.strong)({});
(__VLS_ctx.analysis.recommendedMaterials.join('、'));
__VLS_asFunctionalElement(__VLS_intrinsicElements.ul, __VLS_intrinsicElements.ul)({
    ...{ class: "clean-list jd-suggestions" },
});
for (const [suggestion] of __VLS_getVForSourceType((__VLS_ctx.analysis.suggestions))) {
    __VLS_asFunctionalElement(__VLS_intrinsicElements.li, __VLS_intrinsicElements.li)({
        key: (suggestion),
    });
    (suggestion);
}
var __VLS_27;
/** @type {__VLS_StyleScopedClasses['page-stack']} */ ;
/** @type {__VLS_StyleScopedClasses['module-hero']} */ ;
/** @type {__VLS_StyleScopedClasses['eyebrow']} */ ;
/** @type {__VLS_StyleScopedClasses['page-intro']} */ ;
/** @type {__VLS_StyleScopedClasses['module-hero__status']} */ ;
/** @type {__VLS_StyleScopedClasses['module-grid']} */ ;
/** @type {__VLS_StyleScopedClasses['module-grid--side']} */ ;
/** @type {__VLS_StyleScopedClasses['module-card']} */ ;
/** @type {__VLS_StyleScopedClasses['jd-input-card']} */ ;
/** @type {__VLS_StyleScopedClasses['action-row']} */ ;
/** @type {__VLS_StyleScopedClasses['module-card']} */ ;
/** @type {__VLS_StyleScopedClasses['keyword-cloud']} */ ;
/** @type {__VLS_StyleScopedClasses['match-preview']} */ ;
/** @type {__VLS_StyleScopedClasses['clean-list']} */ ;
/** @type {__VLS_StyleScopedClasses['jd-suggestions']} */ ;
var __VLS_dollars;
const __VLS_self = (await import('vue')).defineComponent({
    setup() {
        return {
            jdContent: jdContent,
            analysis: analysis,
            scoreText: scoreText,
            handleAnalyzeJD: handleAnalyzeJD,
            handleGenerateSuggestion: handleGenerateSuggestion,
        };
    },
});
export default (await import('vue')).defineComponent({
    setup() {
        return {};
    },
});
; /* PartiallyEnd: #4569/main.vue */
