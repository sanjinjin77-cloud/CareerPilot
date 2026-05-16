import { onMounted, ref } from 'vue';
import { ElMessage } from 'element-plus';
import { analyzeAbilityApi } from '@/api/career';
import { abilityReport as mockAbilityReport } from '@/mock/careerPilotMock';
const abilityReport = ref({ ...mockAbilityReport });
async function refreshAbilityReport() {
    try {
        abilityReport.value = await analyzeAbilityApi(abilityReport.value.targetPosition);
        ElMessage.success('已从后端生成能力分析');
    }
    catch {
        ElMessage.warning('后端不可用，继续使用 mock 能力分析');
    }
}
onMounted(refreshAbilityReport);
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
(__VLS_ctx.abilityReport.targetPosition);
__VLS_asFunctionalElement(__VLS_intrinsicElements.em, __VLS_intrinsicElements.em)({});
(__VLS_ctx.abilityReport.suggestions[0]);
const __VLS_0 = {}.ElCard;
/** @type {[typeof __VLS_components.ElCard, typeof __VLS_components.elCard, typeof __VLS_components.ElCard, typeof __VLS_components.elCard, ]} */ ;
// @ts-ignore
const __VLS_1 = __VLS_asFunctionalComponent(__VLS_0, new __VLS_0({
    shadow: "never",
    ...{ class: "module-card" },
}));
const __VLS_2 = __VLS_1({
    shadow: "never",
    ...{ class: "module-card" },
}, ...__VLS_functionalComponentArgsRest(__VLS_1));
__VLS_3.slots.default;
const __VLS_4 = {}.ElRow;
/** @type {[typeof __VLS_components.ElRow, typeof __VLS_components.elRow, typeof __VLS_components.ElRow, typeof __VLS_components.elRow, ]} */ ;
// @ts-ignore
const __VLS_5 = __VLS_asFunctionalComponent(__VLS_4, new __VLS_4({
    gutter: (20),
    align: "middle",
}));
const __VLS_6 = __VLS_5({
    gutter: (20),
    align: "middle",
}, ...__VLS_functionalComponentArgsRest(__VLS_5));
__VLS_7.slots.default;
const __VLS_8 = {}.ElCol;
/** @type {[typeof __VLS_components.ElCol, typeof __VLS_components.elCol, typeof __VLS_components.ElCol, typeof __VLS_components.elCol, ]} */ ;
// @ts-ignore
const __VLS_9 = __VLS_asFunctionalComponent(__VLS_8, new __VLS_8({
    xs: (24),
    md: (8),
    ...{ class: "ability-score" },
}));
const __VLS_10 = __VLS_9({
    xs: (24),
    md: (8),
    ...{ class: "ability-score" },
}, ...__VLS_functionalComponentArgsRest(__VLS_9));
__VLS_11.slots.default;
const __VLS_12 = {}.ElProgress;
/** @type {[typeof __VLS_components.ElProgress, typeof __VLS_components.elProgress, ]} */ ;
// @ts-ignore
const __VLS_13 = __VLS_asFunctionalComponent(__VLS_12, new __VLS_12({
    type: "dashboard",
    percentage: (__VLS_ctx.abilityReport.matchScore),
}));
const __VLS_14 = __VLS_13({
    type: "dashboard",
    percentage: (__VLS_ctx.abilityReport.matchScore),
}, ...__VLS_functionalComponentArgsRest(__VLS_13));
__VLS_asFunctionalElement(__VLS_intrinsicElements.h3, __VLS_intrinsicElements.h3)({});
(__VLS_ctx.abilityReport.targetPosition);
__VLS_asFunctionalElement(__VLS_intrinsicElements.p, __VLS_intrinsicElements.p)({});
var __VLS_11;
const __VLS_16 = {}.ElCol;
/** @type {[typeof __VLS_components.ElCol, typeof __VLS_components.elCol, typeof __VLS_components.ElCol, typeof __VLS_components.elCol, ]} */ ;
// @ts-ignore
const __VLS_17 = __VLS_asFunctionalComponent(__VLS_16, new __VLS_16({
    xs: (24),
    md: (16),
}));
const __VLS_18 = __VLS_17({
    xs: (24),
    md: (16),
}, ...__VLS_functionalComponentArgsRest(__VLS_17));
__VLS_19.slots.default;
const __VLS_20 = {}.ElDescriptions;
/** @type {[typeof __VLS_components.ElDescriptions, typeof __VLS_components.elDescriptions, typeof __VLS_components.ElDescriptions, typeof __VLS_components.elDescriptions, ]} */ ;
// @ts-ignore
const __VLS_21 = __VLS_asFunctionalComponent(__VLS_20, new __VLS_20({
    column: (1),
    border: true,
}));
const __VLS_22 = __VLS_21({
    column: (1),
    border: true,
}, ...__VLS_functionalComponentArgsRest(__VLS_21));
__VLS_23.slots.default;
const __VLS_24 = {}.ElDescriptionsItem;
/** @type {[typeof __VLS_components.ElDescriptionsItem, typeof __VLS_components.elDescriptionsItem, typeof __VLS_components.ElDescriptionsItem, typeof __VLS_components.elDescriptionsItem, ]} */ ;
// @ts-ignore
const __VLS_25 = __VLS_asFunctionalComponent(__VLS_24, new __VLS_24({
    label: "优势",
}));
const __VLS_26 = __VLS_25({
    label: "优势",
}, ...__VLS_functionalComponentArgsRest(__VLS_25));
__VLS_27.slots.default;
(__VLS_ctx.abilityReport.strengths.join('、'));
var __VLS_27;
const __VLS_28 = {}.ElDescriptionsItem;
/** @type {[typeof __VLS_components.ElDescriptionsItem, typeof __VLS_components.elDescriptionsItem, typeof __VLS_components.ElDescriptionsItem, typeof __VLS_components.elDescriptionsItem, ]} */ ;
// @ts-ignore
const __VLS_29 = __VLS_asFunctionalComponent(__VLS_28, new __VLS_28({
    label: "短板",
}));
const __VLS_30 = __VLS_29({
    label: "短板",
}, ...__VLS_functionalComponentArgsRest(__VLS_29));
__VLS_31.slots.default;
(__VLS_ctx.abilityReport.weaknesses.join('、'));
var __VLS_31;
const __VLS_32 = {}.ElDescriptionsItem;
/** @type {[typeof __VLS_components.ElDescriptionsItem, typeof __VLS_components.elDescriptionsItem, typeof __VLS_components.ElDescriptionsItem, typeof __VLS_components.elDescriptionsItem, ]} */ ;
// @ts-ignore
const __VLS_33 = __VLS_asFunctionalComponent(__VLS_32, new __VLS_32({
    label: "优先提升",
}));
const __VLS_34 = __VLS_33({
    label: "优先提升",
}, ...__VLS_functionalComponentArgsRest(__VLS_33));
__VLS_35.slots.default;
(__VLS_ctx.abilityReport.suggestions[0]);
var __VLS_35;
var __VLS_23;
var __VLS_19;
var __VLS_7;
var __VLS_3;
__VLS_asFunctionalElement(__VLS_intrinsicElements.div, __VLS_intrinsicElements.div)({
    ...{ class: "module-grid module-grid--three" },
});
__VLS_asFunctionalElement(__VLS_intrinsicElements.article, __VLS_intrinsicElements.article)({
    ...{ class: "insight-card" },
});
__VLS_asFunctionalElement(__VLS_intrinsicElements.span, __VLS_intrinsicElements.span)({});
__VLS_asFunctionalElement(__VLS_intrinsicElements.strong, __VLS_intrinsicElements.strong)({});
(__VLS_ctx.abilityReport.strengths[0]);
__VLS_asFunctionalElement(__VLS_intrinsicElements.p, __VLS_intrinsicElements.p)({});
__VLS_asFunctionalElement(__VLS_intrinsicElements.article, __VLS_intrinsicElements.article)({
    ...{ class: "insight-card insight-card--active" },
});
__VLS_asFunctionalElement(__VLS_intrinsicElements.span, __VLS_intrinsicElements.span)({});
__VLS_asFunctionalElement(__VLS_intrinsicElements.strong, __VLS_intrinsicElements.strong)({});
(__VLS_ctx.abilityReport.weaknesses[0]);
(__VLS_ctx.abilityReport.weaknesses[1]);
__VLS_asFunctionalElement(__VLS_intrinsicElements.p, __VLS_intrinsicElements.p)({});
__VLS_asFunctionalElement(__VLS_intrinsicElements.article, __VLS_intrinsicElements.article)({
    ...{ class: "insight-card" },
});
__VLS_asFunctionalElement(__VLS_intrinsicElements.span, __VLS_intrinsicElements.span)({});
__VLS_asFunctionalElement(__VLS_intrinsicElements.strong, __VLS_intrinsicElements.strong)({});
__VLS_asFunctionalElement(__VLS_intrinsicElements.p, __VLS_intrinsicElements.p)({});
/** @type {__VLS_StyleScopedClasses['page-stack']} */ ;
/** @type {__VLS_StyleScopedClasses['module-hero']} */ ;
/** @type {__VLS_StyleScopedClasses['eyebrow']} */ ;
/** @type {__VLS_StyleScopedClasses['page-intro']} */ ;
/** @type {__VLS_StyleScopedClasses['module-hero__status']} */ ;
/** @type {__VLS_StyleScopedClasses['module-card']} */ ;
/** @type {__VLS_StyleScopedClasses['ability-score']} */ ;
/** @type {__VLS_StyleScopedClasses['module-grid']} */ ;
/** @type {__VLS_StyleScopedClasses['module-grid--three']} */ ;
/** @type {__VLS_StyleScopedClasses['insight-card']} */ ;
/** @type {__VLS_StyleScopedClasses['insight-card']} */ ;
/** @type {__VLS_StyleScopedClasses['insight-card--active']} */ ;
/** @type {__VLS_StyleScopedClasses['insight-card']} */ ;
var __VLS_dollars;
const __VLS_self = (await import('vue')).defineComponent({
    setup() {
        return {
            abilityReport: abilityReport,
        };
    },
});
export default (await import('vue')).defineComponent({
    setup() {
        return {};
    },
});
; /* PartiallyEnd: #4569/main.vue */
