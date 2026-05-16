import { request } from '@/api/request';
export function planToBlocks(plan) {
    return [
        { title: '未来 3 个月', tasks: plan.short_term_goals },
        { title: '未来 1 年', tasks: plan.mid_term_goals },
        { title: '每周任务', tasks: plan.weekly_tasks },
    ];
}
export function abilityToFrontend(report) {
    return {
        targetPosition: report.target_position,
        matchScore: report.match_score,
        strengths: report.strengths,
        weaknesses: report.weaknesses,
        suggestions: report.suggestions,
    };
}
export function materialToFrontend(material) {
    return {
        id: material.id,
        type: material.type,
        title: material.title,
        originalContent: material.original_content,
        optimizedContent: material.optimized_content,
        tags: material.tags,
    };
}
export function jdToFrontend(analysis) {
    return {
        jobTitle: analysis.job_title,
        companyName: analysis.company_name,
        jdContent: analysis.jd_content,
        keywords: analysis.keywords,
        requirements: analysis.requirements,
        matchScore: analysis.match_score,
        recommendedMaterials: analysis.recommended_materials,
        suggestions: analysis.suggestions,
    };
}
export function interviewToFrontend(record) {
    return {
        jobTitle: record.job_title,
        question: record.question,
        answer: record.answer,
        feedback: record.feedback,
        scores: record.scores,
    };
}
export async function generatePlanApi(payload) {
    const response = await request.post('/planning/generate', {
        stage: payload.stage,
        target_position: payload.targetPosition,
    });
    return response.data;
}
export async function analyzeAbilityApi(targetPosition) {
    const response = await request.post('/ability/analyze', {
        target_position: targetPosition,
    });
    return abilityToFrontend(response.data);
}
export async function getResumeMaterialsApi() {
    const response = await request.get('/resume/materials');
    return response.data.map(materialToFrontend);
}
export async function createResumeMaterialApi(payload) {
    const response = await request.post('/resume/materials', {
        type: payload.type,
        title: payload.title,
        original_content: payload.content,
        tags: payload.tags || [],
    });
    return materialToFrontend(response.data);
}
export async function analyzeJDApi(payload) {
    const response = await request.post('/jd/analyze', {
        job_title: payload.jobTitle,
        company_name: payload.companyName,
        jd_content: payload.jdContent,
    });
    return jdToFrontend(response.data);
}
export async function generateInterviewQuestionApi(jobTitle) {
    const response = await request.post('/interview/question', {
        job_title: jobTitle,
    });
    return response.data;
}
export async function submitInterviewAnswerApi(payload) {
    const response = await request.post('/interview/answer', {
        job_title: payload.jobTitle,
        question: payload.question,
        answer: payload.answer,
    });
    return interviewToFrontend(response.data);
}
