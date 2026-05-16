import { careerPlanBlocks, interviewRecord } from '@/mock/careerPilotMock';
export function calculateProfileCompletion(profile) {
    const fields = [
        profile.grade,
        profile.educationLevel,
        profile.schoolType,
        profile.major,
        profile.currentStage,
        profile.targetPosition,
        profile.targetCity,
        profile.targetIndustry,
        profile.languages,
        profile.frameworks,
        profile.projects,
        profile.competitions,
        profile.internships,
        profile.englishLevel,
        profile.certificates,
    ];
    const filled = fields.filter((value) => String(value || '').trim()).length;
    const preferenceBonus = profile.preferences.length > 0 ? 1 : 0;
    return Math.round(((filled + preferenceBonus) / (fields.length + 1)) * 100);
}
export function saveProfileMock() {
    return { success: true, message: '用户画像已保存到 mock 状态' };
}
export function generatePlanPreviewMock(profile) {
    return `基于你的${profile.grade}、${profile.major}背景和${profile.targetPosition}目标，建议接下来优先完成 1 个可展示项目、补强 TypeScript 与工程化，并沉淀 3 条可写入简历的项目素材。`;
}
export function refreshCareerPlanMock(profile) {
    const blocks = [
        {
            title: '未来 3 个月',
            tasks: [
                `围绕${profile.targetPosition}补齐 TypeScript 基础`,
                '将课程管理系统升级为前后端联调项目',
                '完成 2 次项目复盘并沉淀简历表达',
            ],
        },
        {
            title: '未来 1 年',
            tasks: [
                '完成 1 个可公开展示的综合项目',
                '参与校级比赛或开源协作，补充团队协作经历',
                `针对${profile.targetCity}实习岗位建立投递清单`,
            ],
        },
        {
            title: '每周任务',
            tasks: ['整理 1 条 STAR 项目素材', '分析 1 个真实 JD 的岗位关键词', '完成 1 次模拟面试回答复盘'],
        },
    ];
    return {
        blocks,
        activeStep: 3,
        generatedAt: '已根据最新画像刷新',
    };
}
export function getInitialCareerPlanBlocks() {
    return careerPlanBlocks.map((block) => ({ ...block }));
}
export function optimizeResumeMaterialMock(type, content) {
    return `围绕${type}进行实践，完成${content.replace(/[。！？]$/u, '')}，体现了需求理解、功能实现与问题复盘能力，可作为求职简历中的成长型经历。`;
}
export function createResumeMaterialMock(payload) {
    return {
        id: Date.now(),
        type: payload.type,
        title: payload.title || `${payload.type}素材`,
        originalContent: payload.content,
        optimizedContent: payload.optimizedContent || optimizeResumeMaterialMock(payload.type, payload.content),
        tags: [payload.type, '待完善'],
    };
}
export function analyzeJDMock(content) {
    const keywords = ['Vue', 'TypeScript', '工程化', '项目表达'].filter((keyword) => content.toLowerCase().includes(keyword.toLowerCase()));
    return {
        keywords: keywords.length ? keywords : ['岗位关键词', '能力要求', '项目经验'],
        matchScore: keywords.includes('TypeScript') ? 76 : 68,
        recommendedMaterials: keywords.includes('TypeScript')
            ? ['课程管理系统登录模块', '个人博客前端页面', 'TypeScript 重构经历']
            : ['课程管理系统登录模块', '组件封装经历', '前后端联调项目'],
        suggestions: [
            '突出与 JD 关键词直接相关的项目经历',
            keywords.includes('TypeScript') ? '补充 TypeScript 类型设计和问题处理细节' : '补充目标岗位要求中的核心技能关键词',
            '用 STAR 结构描述项目背景、职责、行动和结果',
        ],
    };
}
export function appendJDOptimizationSuggestionMock(suggestions) {
    return [...suggestions, '建议将最匹配的 2 条素材放在简历项目经历靠前位置'];
}
export const interviewQuestions = [
    interviewRecord.question,
    '你在项目中如何处理组件复用和状态管理？请结合具体经历说明。',
    '如果页面首屏加载较慢，你会从哪些方向排查和优化？',
];
export function scoreInterviewAnswerMock(answer) {
    const lengthBonus = answer.length > 80 ? 8 : 0;
    return {
        scores: {
            technical: Math.min(92, interviewRecord.scores.technical + lengthBonus),
            project: Math.min(92, interviewRecord.scores.project + lengthBonus),
            logic: Math.min(88, interviewRecord.scores.logic + (answer.includes('首先') ? 10 : 4)),
            star: Math.min(86, interviewRecord.scores.star + (answer.includes('结果') ? 12 : 5)),
        },
        feedback: '已完成 mock 评分：回答具备项目背景和个人职责描述，建议继续补充量化结果、技术难点和复盘收获。',
    };
}
