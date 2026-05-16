import { request } from '@/api/request';
import type { AbilityReport, CareerPlanBlock, InterviewRecord, JDAnalysis, ResumeMaterial } from '@/types';

interface CareerPlanResponse {
  id: number;
  stage: string;
  target_position: string;
  short_term_goals: string[];
  mid_term_goals: string[];
  long_term_goals: string[];
  weekly_tasks: string[];
}

interface AbilityResponse {
  target_position: string;
  match_score: number;
  strengths: string[];
  weaknesses: string[];
  suggestions: string[];
}

interface ResumeMaterialResponse {
  id: number;
  type: string;
  title: string;
  original_content: string;
  optimized_content: string;
  tags: string[];
}

interface JDAnalysisResponse {
  job_title: string;
  company_name: string;
  jd_content: string;
  keywords: string[];
  requirements: string[];
  match_score: number;
  recommended_materials: string[];
  suggestions: string[];
}

interface InterviewRecordResponse {
  job_title: string;
  question: string;
  answer: string;
  feedback: string;
  scores: {
    technical: number;
    project: number;
    logic: number;
    star: number;
  };
}

export function planToBlocks(plan: CareerPlanResponse): CareerPlanBlock[] {
  return [
    { title: '未来 3 个月', tasks: plan.short_term_goals },
    { title: '未来 1 年', tasks: plan.mid_term_goals },
    { title: '每周任务', tasks: plan.weekly_tasks },
  ];
}

export function abilityToFrontend(report: AbilityResponse): AbilityReport {
  return {
    targetPosition: report.target_position,
    matchScore: report.match_score,
    strengths: report.strengths,
    weaknesses: report.weaknesses,
    suggestions: report.suggestions,
  };
}

export function materialToFrontend(material: ResumeMaterialResponse): ResumeMaterial {
  return {
    id: material.id,
    type: material.type,
    title: material.title,
    originalContent: material.original_content,
    optimizedContent: material.optimized_content,
    tags: material.tags,
  };
}

export function jdToFrontend(analysis: JDAnalysisResponse): JDAnalysis {
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

export function interviewToFrontend(record: InterviewRecordResponse): InterviewRecord {
  return {
    jobTitle: record.job_title,
    question: record.question,
    answer: record.answer,
    feedback: record.feedback,
    scores: record.scores,
  };
}

export async function generatePlanApi(payload: { stage: string; targetPosition: string }) {
  const response = await request.post<CareerPlanResponse>('/planning/generate', {
    stage: payload.stage,
    target_position: payload.targetPosition,
  });
  return response.data;
}

export async function analyzeAbilityApi(targetPosition: string) {
  const response = await request.post<AbilityResponse>('/ability/analyze', {
    target_position: targetPosition,
  });
  return abilityToFrontend(response.data);
}

export async function getResumeMaterialsApi() {
  const response = await request.get<ResumeMaterialResponse[]>('/resume/materials');
  return response.data.map(materialToFrontend);
}

export async function createResumeMaterialApi(payload: {
  type: string;
  title: string;
  content: string;
  tags?: string[];
}) {
  const response = await request.post<ResumeMaterialResponse>('/resume/materials', {
    type: payload.type,
    title: payload.title,
    original_content: payload.content,
    tags: payload.tags || [],
  });
  return materialToFrontend(response.data);
}

export async function analyzeJDApi(payload: {
  jobTitle: string;
  companyName: string;
  jdContent: string;
}) {
  const response = await request.post<JDAnalysisResponse>('/jd/analyze', {
    job_title: payload.jobTitle,
    company_name: payload.companyName,
    jd_content: payload.jdContent,
  });
  return jdToFrontend(response.data);
}

export async function generateInterviewQuestionApi(jobTitle: string) {
  const response = await request.post<{ job_title: string; question: string }>('/interview/question', {
    job_title: jobTitle,
  });
  return response.data;
}

export async function submitInterviewAnswerApi(payload: {
  jobTitle: string;
  question: string;
  answer: string;
}) {
  const response = await request.post<InterviewRecordResponse>('/interview/answer', {
    job_title: payload.jobTitle,
    question: payload.question,
    answer: payload.answer,
  });
  return interviewToFrontend(response.data);
}
