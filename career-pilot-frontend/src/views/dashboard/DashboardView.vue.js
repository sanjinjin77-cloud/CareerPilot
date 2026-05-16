import AiSuggestionCard from '@/components/AiSuggestionCard.vue';
import FeatureGrid from '@/components/FeatureGrid.vue';
import GrowthRoadmap from '@/components/GrowthRoadmap.vue';
import HeroBanner from '@/components/HeroBanner.vue';
import SummaryCards from '@/components/SummaryCards.vue';
debugger; /* PartiallyEnd: #3632/scriptSetup.vue */
const __VLS_ctx = {};
let __VLS_components;
let __VLS_directives;
__VLS_asFunctionalElement(__VLS_intrinsicElements.section, __VLS_intrinsicElements.section)({
    ...{ class: "home-page" },
});
/** @type {[typeof HeroBanner, ]} */ ;
// @ts-ignore
const __VLS_0 = __VLS_asFunctionalComponent(HeroBanner, new HeroBanner({}));
const __VLS_1 = __VLS_0({}, ...__VLS_functionalComponentArgsRest(__VLS_0));
/** @type {[typeof SummaryCards, ]} */ ;
// @ts-ignore
const __VLS_3 = __VLS_asFunctionalComponent(SummaryCards, new SummaryCards({}));
const __VLS_4 = __VLS_3({}, ...__VLS_functionalComponentArgsRest(__VLS_3));
/** @type {[typeof GrowthRoadmap, ]} */ ;
// @ts-ignore
const __VLS_6 = __VLS_asFunctionalComponent(GrowthRoadmap, new GrowthRoadmap({}));
const __VLS_7 = __VLS_6({}, ...__VLS_functionalComponentArgsRest(__VLS_6));
/** @type {[typeof FeatureGrid, ]} */ ;
// @ts-ignore
const __VLS_9 = __VLS_asFunctionalComponent(FeatureGrid, new FeatureGrid({}));
const __VLS_10 = __VLS_9({}, ...__VLS_functionalComponentArgsRest(__VLS_9));
/** @type {[typeof AiSuggestionCard, ]} */ ;
// @ts-ignore
const __VLS_12 = __VLS_asFunctionalComponent(AiSuggestionCard, new AiSuggestionCard({}));
const __VLS_13 = __VLS_12({}, ...__VLS_functionalComponentArgsRest(__VLS_12));
/** @type {__VLS_StyleScopedClasses['home-page']} */ ;
var __VLS_dollars;
const __VLS_self = (await import('vue')).defineComponent({
    setup() {
        return {
            AiSuggestionCard: AiSuggestionCard,
            FeatureGrid: FeatureGrid,
            GrowthRoadmap: GrowthRoadmap,
            HeroBanner: HeroBanner,
            SummaryCards: SummaryCards,
        };
    },
});
export default (await import('vue')).defineComponent({
    setup() {
        return {};
    },
});
; /* PartiallyEnd: #4569/main.vue */
