import { request } from '@/api/request';
import type {
  AbilityReport,
  CareerPlanBlock,
  InterviewRecord,
  InterviewQuestion,
  InterviewImprovementPlan,
  JDAnalysis,
  JobMatch,
  JobPosition,
  LearningPlan,
  LearningTask,
  ResumeDocument,
  ResumeMaterial,
  TargetOffer,
} from '@/types';

interface ResultResponse<T> {
  code: number;
  message: string;
  data: T;
}

interface CareerPlanResponse {
  id: number;
  stage: string;
  target_position: string;
  current_level: string;
  short_term_goals: string[];
  mid_term_goals: string[];
  long_term_goals: string[];
  weekly_tasks: string[];
  roadmap: string[];
  generation_source: 'llm' | 'local_fallback';
  model?: string;
}

interface AbilityResponse {
  target_position: string;
  match_score: number;
  strengths: string[];
  weaknesses: string[];
  suggestions: string[];
  summary: string;
  skill_scores: Record<string, number>;
  analysis_source: 'llm' | 'local_fallback';
  model?: string;
}

interface LearningPlanResponse {
  id: number;
  current_stage: string;
  total_period: string;
  weekly_hours: number;
  goal_summary: string;
  stage_tasks: string;
  skill_tasks: string;
  project_tasks: string;
  resume_tasks: string;
  interview_tasks: string;
  progress: number;
  generation_source: 'llm' | 'local_fallback';
  model?: string;
}

interface LearningTaskResponse {
  id: number;
  plan_id: number;
  task_type: string;
  title: string;
  description: string;
  priority: string;
  status: string;
  due_date: string;
  completed_at?: string;
}

interface ResumeMaterialResponse {
  id: number;
  type: string;
  title: string;
  original_content: string;
  optimized_content: string;
  tags: string[];
  source_task_id?: number;
  source_type?: string;
  optimization_source?: 'llm' | 'local_fallback' | '';
  model?: string;
}

interface ResumeDocumentResponse {
  id: number;
  title: string;
  source_type: string;
  content: string;
  optimized_content: string;
  status: string;
  matched_materials: string[];
  keywords: string[];
  generation_source: 'llm' | 'local_fallback' | '';
  model?: string;
  created_at?: string;
  updated_at?: string;
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
  analysis_source: 'llm' | 'local_fallback';
  model?: string;
}

interface InterviewRecordResponse {
  id?: number;
  job_title: string;
  resume_document_id?: number;
  resume_title?: string;
  question: string;
  answer: string;
  feedback: string;
  scores: {
    technical: number;
    project: number;
    logic: number;
    star: number;
  };
  evaluation_source: 'llm' | 'local_fallback' | 'history';
  model?: string;
  created_at?: string;
}

interface InterviewQuestionResponse {
  job_title: string;
  resume_document_id?: number;
  resume_title: string;
  question: string;
  keywords: string[];
  generation_source: 'llm' | 'local_fallback';
  model?: string;
}

interface InterviewImprovementResponse {
  job_title: string;
  average_score: number;
  focus_areas: string[];
  summary: string;
  tasks: LearningTaskResponse[];
}

interface JobPositionResponse {
  id: number;
  jobTitle: string;
  companyName: string;
  jobCategory: string;
  city: string;
  salaryMin: number;
  salaryMax: number;
  educationRequirement: string;
  experienceRequirement: string;
  jobType: string;
  techStack: string;
  jobDescription: string;
  jobRequirement: string;
  companyType: string;
  workMode: string;
  publishTime?: string;
  source?: string;
}

interface TargetOfferResponse {
  id: number;
  userId: number;
  jobId?: number;
  jobTitle: string;
  companyName: string;
  targetRole: string;
  targetCity: string;
  targetSalary: string;
  requiredSkills: string;
  jobRequirements: string;
  matchScore: number;
  status: string;
}

interface JobMatchResponse {
  job: JobPositionResponse;
  matchScore: number;
  matchReasons: string[];
}

function decodeMaybeMojibake(value?: string) {
  if (!value) return '';

  let current = value;
  for (let index = 0; index < 4; index += 1) {
    try {
      const bytes = new Uint8Array(Array.from(current, (char) => char.charCodeAt(0) & 0xff));
      const decoded = new TextDecoder('utf-8', { fatal: true }).decode(bytes);
      const decodedChinese = decoded.match(/[\u4e00-\u9fff]/g)?.length || 0;
      const currentChinese = current.match(/[\u4e00-\u9fff]/g)?.length || 0;
      if (decodedChinese <= currentChinese || decoded === current) break;
      current = decoded;
    } catch {
      break;
    }
  }
  return current;
}

function decodeList(values: string[] = []) {
  return values.map(decodeMaybeMojibake);
}

export function planToBlocks(plan: CareerPlanResponse): CareerPlanBlock[] {
  return [
    { title: '近期目标', tasks: decodeList(plan.short_term_goals) },
    { title: '未来 3 个月', tasks: decodeList(plan.mid_term_goals) },
    { title: '未来 1 年', tasks: decodeList(plan.long_term_goals) },
    { title: '每周行动', tasks: decodeList(plan.weekly_tasks) },
  ];
}

export function abilityToFrontend(report: AbilityResponse): AbilityReport {
  return {
    targetPosition: report.target_position,
    matchScore: report.match_score,
    strengths: report.strengths,
    weaknesses: report.weaknesses,
    suggestions: report.suggestions,
    summary: report.summary,
    skillScores: report.skill_scores,
    analysisSource: report.analysis_source,
    model: report.model,
  };
}

export function learningPlanToFrontend(plan: LearningPlanResponse): LearningPlan {
  return {
    id: plan.id,
    currentStage: plan.current_stage,
    totalPeriod: plan.total_period,
    weeklyHours: plan.weekly_hours,
    goalSummary: decodeMaybeMojibake(plan.goal_summary),
    stageTasks: decodeMaybeMojibake(plan.stage_tasks),
    skillTasks: decodeMaybeMojibake(plan.skill_tasks),
    projectTasks: decodeMaybeMojibake(plan.project_tasks),
    resumeTasks: decodeMaybeMojibake(plan.resume_tasks),
    interviewTasks: decodeMaybeMojibake(plan.interview_tasks),
    progress: plan.progress,
    generationSource: plan.generation_source,
    model: plan.model,
  };
}

export function learningTaskToFrontend(task: LearningTaskResponse): LearningTask {
  return {
    id: task.id,
    planId: task.plan_id,
    taskType: task.task_type,
    title: decodeMaybeMojibake(task.title),
    description: decodeMaybeMojibake(task.description),
    priority: task.priority,
    status: task.status,
    dueDate: task.due_date,
    completedAt: task.completed_at,
  };
}

export function materialToFrontend(material: ResumeMaterialResponse): ResumeMaterial {
  return {
    id: material.id,
    type: material.type,
    title: decodeMaybeMojibake(material.title),
    originalContent: decodeMaybeMojibake(material.original_content),
    optimizedContent: decodeMaybeMojibake(material.optimized_content),
    tags: decodeList(material.tags),
    sourceTaskId: material.source_task_id,
    sourceType: material.source_type,
    optimizationSource: material.optimization_source,
    model: material.model,
  };
}

export function resumeDocumentToFrontend(document: ResumeDocumentResponse): ResumeDocument {
  return {
    id: document.id,
    title: decodeMaybeMojibake(document.title),
    sourceType: document.source_type,
    content: decodeMaybeMojibake(document.content),
    optimizedContent: decodeMaybeMojibake(document.optimized_content),
    status: document.status,
    matchedMaterials: decodeList(document.matched_materials),
    keywords: decodeList(document.keywords),
    generationSource: document.generation_source || 'local_fallback',
    model: document.model,
    createdAt: document.created_at,
    updatedAt: document.updated_at,
  };
}

export function jdToFrontend(analysis: JDAnalysisResponse): JDAnalysis {
  return {
    jobTitle: decodeMaybeMojibake(analysis.job_title),
    companyName: decodeMaybeMojibake(analysis.company_name),
    jdContent: decodeMaybeMojibake(analysis.jd_content),
    keywords: decodeList(analysis.keywords),
    requirements: decodeList(analysis.requirements),
    matchScore: analysis.match_score,
    recommendedMaterials: decodeList(analysis.recommended_materials),
    suggestions: decodeList(analysis.suggestions),
    analysisSource: analysis.analysis_source,
    model: analysis.model,
  };
}

export function interviewToFrontend(record: InterviewRecordResponse): InterviewRecord {
  return {
    id: record.id,
    jobTitle: decodeMaybeMojibake(record.job_title),
    resumeDocumentId: record.resume_document_id,
    resumeTitle: decodeMaybeMojibake(record.resume_title),
    question: decodeMaybeMojibake(record.question),
    answer: decodeMaybeMojibake(record.answer),
    feedback: decodeMaybeMojibake(record.feedback),
    scores: record.scores,
    evaluationSource: record.evaluation_source,
    model: record.model,
    createdAt: record.created_at,
  };
}

export function interviewQuestionToFrontend(question: InterviewQuestionResponse): InterviewQuestion {
  return {
    jobTitle: decodeMaybeMojibake(question.job_title),
    resumeDocumentId: question.resume_document_id,
    resumeTitle: decodeMaybeMojibake(question.resume_title),
    question: decodeMaybeMojibake(question.question),
    keywords: decodeList(question.keywords),
    generationSource: question.generation_source,
    model: question.model,
  };
}

export function interviewImprovementToFrontend(plan: InterviewImprovementResponse): InterviewImprovementPlan {
  return {
    jobTitle: decodeMaybeMojibake(plan.job_title),
    averageScore: plan.average_score,
    focusAreas: decodeList(plan.focus_areas),
    summary: decodeMaybeMojibake(plan.summary),
    tasks: plan.tasks.map(learningTaskToFrontend),
  };
}

export function jobPositionToFrontend(job: JobPositionResponse): JobPosition {
  return {
    id: job.id,
    jobTitle: decodeMaybeMojibake(job.jobTitle),
    companyName: decodeMaybeMojibake(job.companyName),
    jobCategory: decodeMaybeMojibake(job.jobCategory),
    city: decodeMaybeMojibake(job.city),
    salaryMin: job.salaryMin,
    salaryMax: job.salaryMax,
    educationRequirement: decodeMaybeMojibake(job.educationRequirement),
    experienceRequirement: decodeMaybeMojibake(job.experienceRequirement),
    jobType: decodeMaybeMojibake(job.jobType),
    techStack: decodeMaybeMojibake(job.techStack),
    jobDescription: decodeMaybeMojibake(job.jobDescription),
    jobRequirement: decodeMaybeMojibake(job.jobRequirement),
    companyType: decodeMaybeMojibake(job.companyType),
    workMode: decodeMaybeMojibake(job.workMode),
    publishTime: job.publishTime,
    source: job.source,
  };
}

export function targetOfferToFrontend(offer: TargetOfferResponse | null): TargetOffer | null {
  if (!offer) return null;
  return {
    id: offer.id,
    userId: offer.userId,
    jobId: offer.jobId,
    jobTitle: decodeMaybeMojibake(offer.jobTitle),
    companyName: decodeMaybeMojibake(offer.companyName),
    targetRole: decodeMaybeMojibake(offer.targetRole),
    targetCity: decodeMaybeMojibake(offer.targetCity),
    targetSalary: decodeMaybeMojibake(offer.targetSalary),
    requiredSkills: decodeMaybeMojibake(offer.requiredSkills),
    jobRequirements: decodeMaybeMojibake(offer.jobRequirements),
    matchScore: offer.matchScore,
    status: offer.status,
  };
}

export function jobMatchToFrontend(match: JobMatchResponse): JobMatch {
  return {
    job: jobPositionToFrontend(match.job),
    matchScore: match.matchScore,
    matchReasons: decodeList(match.matchReasons),
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

export async function generateLearningPlanApi() {
  const response = await request.post<LearningPlanResponse>('/learning/plan/generate');
  return learningPlanToFrontend(response.data);
}

export async function getCurrentLearningPlanApi() {
  const response = await request.get<LearningPlanResponse | null>('/learning/plan/current');
  return response.data ? learningPlanToFrontend(response.data) : null;
}

export async function getLearningTasksApi() {
  const response = await request.get<LearningTaskResponse[]>('/learning/tasks');
  return response.data.map(learningTaskToFrontend);
}

export async function completeLearningTaskApi(taskId: number) {
  const response = await request.post<LearningTaskResponse>(`/learning/tasks/${taskId}/complete`);
  return learningTaskToFrontend(response.data);
}

export async function createMaterialFromLearningTaskApi(taskId: number) {
  const response = await request.post<ResumeMaterialResponse>(`/learning/tasks/${taskId}/material`);
  return materialToFrontend(response.data);
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

export async function updateResumeMaterialApi(
  materialId: number,
  payload: {
    type: string;
    title: string;
    content: string;
    tags?: string[];
  },
) {
  const response = await request.put<ResumeMaterialResponse>(`/resume/materials/${materialId}`, {
    type: payload.type,
    title: payload.title,
    original_content: payload.content,
    tags: payload.tags || [],
  });
  return materialToFrontend(response.data);
}

export async function optimizeResumeMaterialApi(materialId: number) {
  const response = await request.post<ResumeMaterialResponse>(`/resume/materials/${materialId}/optimize`);
  return materialToFrontend(response.data);
}

export async function deleteResumeMaterialApi(materialId: number) {
  await request.delete(`/resume/materials/${materialId}`);
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

export async function customizeResumeApi(payload: {
  jobTitle: string;
  companyName: string;
  jdContent: string;
}) {
  const response = await request.post<ResumeDocumentResponse>('/resume/customize', {
    job_title: payload.jobTitle,
    company_name: payload.companyName,
    jd_content: payload.jdContent,
  });
  return resumeDocumentToFrontend(response.data);
}

export async function getResumeDocumentsApi() {
  const response = await request.get<ResumeDocumentResponse[]>('/resume/documents');
  return response.data.map(resumeDocumentToFrontend);
}

export async function getResumeDocumentApi(documentId: number) {
  const response = await request.get<ResumeDocumentResponse>(`/resume/documents/${documentId}`);
  return resumeDocumentToFrontend(response.data);
}

export async function updateResumeDocumentApi(
  documentId: number,
  payload: {
    title: string;
    content: string;
    optimizedContent: string;
    status: string;
  },
) {
  const response = await request.put<ResumeDocumentResponse>(`/resume/documents/${documentId}`, {
    title: payload.title,
    content: payload.content,
    optimized_content: payload.optimizedContent,
    status: payload.status,
  });
  return resumeDocumentToFrontend(response.data);
}

export async function deleteResumeDocumentApi(documentId: number) {
  await request.delete(`/resume/documents/${documentId}`);
}

export async function generateInterviewQuestionApi(payload: { jobTitle: string; resumeDocumentId?: number }) {
  const response = await request.post<InterviewQuestionResponse>('/interview/question', {
    job_title: payload.jobTitle,
    resume_document_id: payload.resumeDocumentId,
  });
  return interviewQuestionToFrontend(response.data);
}

export async function submitInterviewAnswerApi(payload: {
  jobTitle: string;
  resumeDocumentId?: number;
  question: string;
  answer: string;
}) {
  const response = await request.post<InterviewRecordResponse>('/interview/answer', {
    job_title: payload.jobTitle,
    resume_document_id: payload.resumeDocumentId,
    question: payload.question,
    answer: payload.answer,
  });
  return interviewToFrontend(response.data);
}

export async function getInterviewRecordsApi() {
  const response = await request.get<InterviewRecordResponse[]>('/interview/records');
  return response.data.map(interviewToFrontend);
}

export async function deleteInterviewRecordApi(recordId: number) {
  await request.delete(`/interview/records/${recordId}`);
}

export async function createInterviewImprovementPlanApi() {
  const response = await request.post<InterviewImprovementResponse>('/learning/interview-review');
  return interviewImprovementToFrontend(response.data);
}

export async function getJobsApi(filters: {
  keyword?: string;
  category?: string;
  city?: string;
  jobType?: string;
  techStack?: string;
} = {}) {
  const response = await request.get<ResultResponse<JobPositionResponse[]>>('/jobs', {
    params: {
      keyword: filters.keyword || undefined,
      category: filters.category || undefined,
      city: filters.city || undefined,
      jobType: filters.jobType || undefined,
      techStack: filters.techStack || undefined,
    },
  });
  return response.data.data.map(jobPositionToFrontend);
}

export async function getJobMatchesApi() {
  const response = await request.get<ResultResponse<JobMatchResponse[]>>('/career/job-match');
  return response.data.data.map(jobMatchToFrontend);
}

export async function getCurrentTargetOfferApi() {
  const response = await request.get<ResultResponse<TargetOfferResponse | null>>('/target-offer/current');
  return targetOfferToFrontend(response.data.data);
}

export async function setTargetOfferApi(jobId: number) {
  const response = await request.post<ResultResponse<TargetOfferResponse>>(`/target-offer/${jobId}`);
  return targetOfferToFrontend(response.data.data);
}
