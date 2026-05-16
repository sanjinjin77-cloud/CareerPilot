import { computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useAuthStore } from '@/stores/auth';
const route = useRoute();
const router = useRouter();
const authStore = useAuthStore();
const navItems = [
    { label: '首页', path: '/' },
    { label: '个人画像', path: '/profile' },
    { label: '学习规划', path: '/learning-plan' },
    {
        label: '职业规划',
        path: '/career/job-match',
        children: [
            { label: '岗位匹配', path: '/career/job-match' },
            { label: '岗位筛选', path: '/career/job-filter' },
        ],
    },
    { label: '能力分析', path: '/ability' },
    {
        label: '我的简历',
        path: '/resume/materials',
        children: [
            { label: '简历素材', path: '/resume/materials' },
            { label: '简历模板', path: '/resume/templates' },
        ],
    },
    { label: '模拟面试', path: '/interview' },
];
const activePath = computed(() => route.path);
function isActive(item) {
    if (item.path === '/') {
        return activePath.value === '/';
    }
    return activePath.value === item.path
        || activePath.value.startsWith(`${item.path}/`)
        || Boolean(item.children?.some((child) => activePath.value === child.path));
}
function handleLogout() {
    authStore.logout();
    router.push('/login');
}
debugger; /* PartiallyEnd: #3632/scriptSetup.vue */
const __VLS_ctx = {};
let __VLS_components;
let __VLS_directives;
__VLS_asFunctionalElement(__VLS_intrinsicElements.header, __VLS_intrinsicElements.header)({
    ...{ class: "app-header" },
});
const __VLS_0 = {}.RouterLink;
/** @type {[typeof __VLS_components.RouterLink, typeof __VLS_components.routerLink, typeof __VLS_components.RouterLink, typeof __VLS_components.routerLink, ]} */ ;
// @ts-ignore
const __VLS_1 = __VLS_asFunctionalComponent(__VLS_0, new __VLS_0({
    ...{ class: "app-header__brand" },
    to: "/",
}));
const __VLS_2 = __VLS_1({
    ...{ class: "app-header__brand" },
    to: "/",
}, ...__VLS_functionalComponentArgsRest(__VLS_1));
__VLS_3.slots.default;
__VLS_asFunctionalElement(__VLS_intrinsicElements.span, __VLS_intrinsicElements.span)({
    ...{ class: "app-header__logo" },
});
__VLS_asFunctionalElement(__VLS_intrinsicElements.span, __VLS_intrinsicElements.span)({});
__VLS_asFunctionalElement(__VLS_intrinsicElements.strong, __VLS_intrinsicElements.strong)({});
__VLS_asFunctionalElement(__VLS_intrinsicElements.small, __VLS_intrinsicElements.small)({});
var __VLS_3;
__VLS_asFunctionalElement(__VLS_intrinsicElements.nav, __VLS_intrinsicElements.nav)({
    ...{ class: "app-header__nav" },
});
for (const [item] of __VLS_getVForSourceType((__VLS_ctx.navItems))) {
    __VLS_asFunctionalElement(__VLS_intrinsicElements.div, __VLS_intrinsicElements.div)({
        key: (item.path),
        ...{ class: "app-header__nav-item" },
        ...{ class: ({ active: __VLS_ctx.isActive(item), 'has-dropdown': item.children }) },
    });
    const __VLS_4 = {}.RouterLink;
    /** @type {[typeof __VLS_components.RouterLink, typeof __VLS_components.routerLink, typeof __VLS_components.RouterLink, typeof __VLS_components.routerLink, ]} */ ;
    // @ts-ignore
    const __VLS_5 = __VLS_asFunctionalComponent(__VLS_4, new __VLS_4({
        to: (item.path),
    }));
    const __VLS_6 = __VLS_5({
        to: (item.path),
    }, ...__VLS_functionalComponentArgsRest(__VLS_5));
    __VLS_7.slots.default;
    (item.label);
    var __VLS_7;
    if (item.children) {
        __VLS_asFunctionalElement(__VLS_intrinsicElements.div, __VLS_intrinsicElements.div)({
            ...{ class: "app-header__dropdown" },
        });
        for (const [child] of __VLS_getVForSourceType((item.children))) {
            const __VLS_8 = {}.RouterLink;
            /** @type {[typeof __VLS_components.RouterLink, typeof __VLS_components.routerLink, typeof __VLS_components.RouterLink, typeof __VLS_components.routerLink, ]} */ ;
            // @ts-ignore
            const __VLS_9 = __VLS_asFunctionalComponent(__VLS_8, new __VLS_8({
                key: (child.path),
                to: (child.path),
                ...{ class: ({ active: __VLS_ctx.activePath === child.path }) },
            }));
            const __VLS_10 = __VLS_9({
                key: (child.path),
                to: (child.path),
                ...{ class: ({ active: __VLS_ctx.activePath === child.path }) },
            }, ...__VLS_functionalComponentArgsRest(__VLS_9));
            __VLS_11.slots.default;
            (child.label);
            var __VLS_11;
        }
    }
}
__VLS_asFunctionalElement(__VLS_intrinsicElements.div, __VLS_intrinsicElements.div)({
    ...{ class: "app-header__user" },
});
const __VLS_12 = {}.RouterLink;
/** @type {[typeof __VLS_components.RouterLink, typeof __VLS_components.routerLink, typeof __VLS_components.RouterLink, typeof __VLS_components.routerLink, ]} */ ;
// @ts-ignore
const __VLS_13 = __VLS_asFunctionalComponent(__VLS_12, new __VLS_12({
    ...{ class: "app-header__profile" },
    to: "/profile",
}));
const __VLS_14 = __VLS_13({
    ...{ class: "app-header__profile" },
    to: "/profile",
}, ...__VLS_functionalComponentArgsRest(__VLS_13));
__VLS_15.slots.default;
__VLS_asFunctionalElement(__VLS_intrinsicElements.span, __VLS_intrinsicElements.span)({
    ...{ class: "app-header__avatar" },
});
__VLS_asFunctionalElement(__VLS_intrinsicElements.span, __VLS_intrinsicElements.span)({});
var __VLS_15;
const __VLS_16 = {}.ElButton;
/** @type {[typeof __VLS_components.ElButton, typeof __VLS_components.elButton, typeof __VLS_components.ElButton, typeof __VLS_components.elButton, ]} */ ;
// @ts-ignore
const __VLS_17 = __VLS_asFunctionalComponent(__VLS_16, new __VLS_16({
    ...{ 'onClick': {} },
    plain: true,
    type: "success",
}));
const __VLS_18 = __VLS_17({
    ...{ 'onClick': {} },
    plain: true,
    type: "success",
}, ...__VLS_functionalComponentArgsRest(__VLS_17));
let __VLS_20;
let __VLS_21;
let __VLS_22;
const __VLS_23 = {
    onClick: (__VLS_ctx.handleLogout)
};
__VLS_19.slots.default;
var __VLS_19;
/** @type {__VLS_StyleScopedClasses['app-header']} */ ;
/** @type {__VLS_StyleScopedClasses['app-header__brand']} */ ;
/** @type {__VLS_StyleScopedClasses['app-header__logo']} */ ;
/** @type {__VLS_StyleScopedClasses['app-header__nav']} */ ;
/** @type {__VLS_StyleScopedClasses['app-header__nav-item']} */ ;
/** @type {__VLS_StyleScopedClasses['app-header__dropdown']} */ ;
/** @type {__VLS_StyleScopedClasses['app-header__user']} */ ;
/** @type {__VLS_StyleScopedClasses['app-header__profile']} */ ;
/** @type {__VLS_StyleScopedClasses['app-header__avatar']} */ ;
var __VLS_dollars;
const __VLS_self = (await import('vue')).defineComponent({
    setup() {
        return {
            navItems: navItems,
            activePath: activePath,
            isActive: isActive,
            handleLogout: handleLogout,
        };
    },
});
export default (await import('vue')).defineComponent({
    setup() {
        return {};
    },
});
; /* PartiallyEnd: #4569/main.vue */
