import { ref } from 'vue';
import { ElMessage } from 'element-plus';
import { generatePlanApi, planToBlocks } from '@/api/career';
import { mockProfile } from '@/mock/careerPilotMock';
import { getInitialCareerPlanBlocks, refreshCareerPlanMock } from '@/services/mockCareerService';
const planBlocks = ref(getInitialCareerPlanBlocks());
const generatedAt = ref('刚刚生成');
const activeStep = ref(2);
const loading = ref(false);
async function handleRefreshPlan() {
    try {
        loading.value = true;
        const plan = await generatePlanApi({
            stage: mockProfile.currentStage,
            targetPosition: mockProfile.targetPosition,
        });
        planBlocks.value = planToBlocks(plan);
        generatedAt.value = '已从后端生成';
        activeStep.value = 3;
        ElMessage.success('已从后端生成职业规划');
    }
    catch {
        const result = refreshCareerPlanMock(mockProfile);
        planBlocks.value = result.blocks;
        generatedAt.value = result.generatedAt;
        activeStep.value = result.activeStep;
        ElMessage.warning('后端不可用，已使用 mock 职业规划');
    }
    finally {
        loading.value = false;
    }
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
(__VLS_ctx.mockProfile.currentStage);
__VLS_asFunctionalElement(__VLS_intrinsicElements.em, __VLS_intrinsicElements.em)({});
(__VLS_ctx.generatedAt);
(__VLS_ctx.mockProfile.targetPosition);
const __VLS_0 = {}.ElCard;
/** @type {[typeof __VLS_components.ElCard, typeof __VLS_components.elCard, typeof __VLS_components.ElCard, typeof __VLS_components.elCard, ]} */ ;
// @ts-ignore
const __VLS_1 = __VLS_asFunctionalComponent(__VLS_0, new __VLS_0({
    shadow: "never",
    ...{ class: "module-card plan-timeline" },
}));
const __VLS_2 = __VLS_1({
    shadow: "never",
    ...{ class: "module-card plan-timeline" },
}, ...__VLS_functionalComponentArgsRest(__VLS_1));
__VLS_3.slots.default;
{
    const { header: __VLS_thisSlot } = __VLS_3.slots;
    __VLS_asFunctionalElement(__VLS_intrinsicElements.div, __VLS_intrinsicElements.div)({
        ...{ class: "card-header-action" },
    });
    __VLS_asFunctionalElement(__VLS_intrinsicElements.span, __VLS_intrinsicElements.span)({});
    const __VLS_4 = {}.ElButton;
    /** @type {[typeof __VLS_components.ElButton, typeof __VLS_components.elButton, typeof __VLS_components.ElButton, typeof __VLS_components.elButton, ]} */ ;
    // @ts-ignore
    const __VLS_5 = __VLS_asFunctionalComponent(__VLS_4, new __VLS_4({
        ...{ 'onClick': {} },
        type: "primary",
        plain: true,
        loading: (__VLS_ctx.loading),
    }));
    const __VLS_6 = __VLS_5({
        ...{ 'onClick': {} },
        type: "primary",
        plain: true,
        loading: (__VLS_ctx.loading),
    }, ...__VLS_functionalComponentArgsRest(__VLS_5));
    let __VLS_8;
    let __VLS_9;
    let __VLS_10;
    const __VLS_11 = {
        onClick: (__VLS_ctx.handleRefreshPlan)
    };
    __VLS_7.slots.default;
    var __VLS_7;
}
const __VLS_12 = {}.ElSteps;
/** @type {[typeof __VLS_components.ElSteps, typeof __VLS_components.elSteps, typeof __VLS_components.ElSteps, typeof __VLS_components.elSteps, ]} */ ;
// @ts-ignore
const __VLS_13 = __VLS_asFunctionalComponent(__VLS_12, new __VLS_12({
    active: (__VLS_ctx.activeStep),
    finishStatus: "success",
    alignCenter: true,
}));
const __VLS_14 = __VLS_13({
    active: (__VLS_ctx.activeStep),
    finishStatus: "success",
    alignCenter: true,
}, ...__VLS_functionalComponentArgsRest(__VLS_13));
__VLS_15.slots.default;
const __VLS_16 = {}.ElStep;
/** @type {[typeof __VLS_components.ElStep, typeof __VLS_components.elStep, ]} */ ;
// @ts-ignore
const __VLS_17 = __VLS_asFunctionalComponent(__VLS_16, new __VLS_16({
    title: "职业认知",
}));
const __VLS_18 = __VLS_17({
    title: "职业认知",
}, ...__VLS_functionalComponentArgsRest(__VLS_17));
const __VLS_20 = {}.ElStep;
/** @type {[typeof __VLS_components.ElStep, typeof __VLS_components.elStep, ]} */ ;
// @ts-ignore
const __VLS_21 = __VLS_asFunctionalComponent(__VLS_20, new __VLS_20({
    title: "技能学习",
}));
const __VLS_22 = __VLS_21({
    title: "技能学习",
}, ...__VLS_functionalComponentArgsRest(__VLS_21));
const __VLS_24 = {}.ElStep;
/** @type {[typeof __VLS_components.ElStep, typeof __VLS_components.elStep, ]} */ ;
// @ts-ignore
const __VLS_25 = __VLS_asFunctionalComponent(__VLS_24, new __VLS_24({
    title: "项目积累",
}));
const __VLS_26 = __VLS_25({
    title: "项目积累",
}, ...__VLS_functionalComponentArgsRest(__VLS_25));
const __VLS_28 = {}.ElStep;
/** @type {[typeof __VLS_components.ElStep, typeof __VLS_components.elStep, ]} */ ;
// @ts-ignore
const __VLS_29 = __VLS_asFunctionalComponent(__VLS_28, new __VLS_28({
    title: "简历优化",
}));
const __VLS_30 = __VLS_29({
    title: "简历优化",
}, ...__VLS_functionalComponentArgsRest(__VLS_29));
const __VLS_32 = {}.ElStep;
/** @type {[typeof __VLS_components.ElStep, typeof __VLS_components.elStep, ]} */ ;
// @ts-ignore
const __VLS_33 = __VLS_asFunctionalComponent(__VLS_32, new __VLS_32({
    title: "模拟面试",
}));
const __VLS_34 = __VLS_33({
    title: "模拟面试",
}, ...__VLS_functionalComponentArgsRest(__VLS_33));
var __VLS_15;
var __VLS_3;
const __VLS_36 = {}.ElRow;
/** @type {[typeof __VLS_components.ElRow, typeof __VLS_components.elRow, typeof __VLS_components.ElRow, typeof __VLS_components.elRow, ]} */ ;
// @ts-ignore
const __VLS_37 = __VLS_asFunctionalComponent(__VLS_36, new __VLS_36({
    gutter: (22),
}));
const __VLS_38 = __VLS_37({
    gutter: (22),
}, ...__VLS_functionalComponentArgsRest(__VLS_37));
__VLS_39.slots.default;
for (const [block] of __VLS_getVForSourceType((__VLS_ctx.planBlocks))) {
    const __VLS_40 = {}.ElCol;
    /** @type {[typeof __VLS_components.ElCol, typeof __VLS_components.elCol, typeof __VLS_components.ElCol, typeof __VLS_components.elCol, ]} */ ;
    // @ts-ignore
    const __VLS_41 = __VLS_asFunctionalComponent(__VLS_40, new __VLS_40({
        key: (block.title),
        xs: (24),
        md: (8),
    }));
    const __VLS_42 = __VLS_41({
        key: (block.title),
        xs: (24),
        md: (8),
    }, ...__VLS_functionalComponentArgsRest(__VLS_41));
    __VLS_43.slots.default;
    const __VLS_44 = {}.ElCard;
    /** @type {[typeof __VLS_components.ElCard, typeof __VLS_components.elCard, typeof __VLS_components.ElCard, typeof __VLS_components.elCard, ]} */ ;
    // @ts-ignore
    const __VLS_45 = __VLS_asFunctionalComponent(__VLS_44, new __VLS_44({
        shadow: "never",
        ...{ class: "module-card stage-card" },
    }));
    const __VLS_46 = __VLS_45({
        shadow: "never",
        ...{ class: "module-card stage-card" },
    }, ...__VLS_functionalComponentArgsRest(__VLS_45));
    __VLS_47.slots.default;
    {
        const { header: __VLS_thisSlot } = __VLS_47.slots;
        (block.title);
    }
    __VLS_asFunctionalElement(__VLS_intrinsicElements.ul, __VLS_intrinsicElements.ul)({
        ...{ class: "clean-list" },
    });
    for (const [task] of __VLS_getVForSourceType((block.tasks))) {
        __VLS_asFunctionalElement(__VLS_intrinsicElements.li, __VLS_intrinsicElements.li)({
            key: (task),
        });
        (task);
    }
    var __VLS_47;
    var __VLS_43;
}
var __VLS_39;
/** @type {__VLS_StyleScopedClasses['page-stack']} */ ;
/** @type {__VLS_StyleScopedClasses['module-hero']} */ ;
/** @type {__VLS_StyleScopedClasses['eyebrow']} */ ;
/** @type {__VLS_StyleScopedClasses['page-intro']} */ ;
/** @type {__VLS_StyleScopedClasses['module-hero__status']} */ ;
/** @type {__VLS_StyleScopedClasses['module-card']} */ ;
/** @type {__VLS_StyleScopedClasses['plan-timeline']} */ ;
/** @type {__VLS_StyleScopedClasses['card-header-action']} */ ;
/** @type {__VLS_StyleScopedClasses['module-card']} */ ;
/** @type {__VLS_StyleScopedClasses['stage-card']} */ ;
/** @type {__VLS_StyleScopedClasses['clean-list']} */ ;
var __VLS_dollars;
const __VLS_self = (await import('vue')).defineComponent({
    setup() {
        return {
            mockProfile: mockProfile,
            planBlocks: planBlocks,
            generatedAt: generatedAt,
            activeStep: activeStep,
            loading: loading,
            handleRefreshPlan: handleRefreshPlan,
        };
    },
});
export default (await import('vue')).defineComponent({
    setup() {
        return {};
    },
});
; /* PartiallyEnd: #4569/main.vue */
