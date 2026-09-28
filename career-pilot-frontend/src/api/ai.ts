import { request } from '@/api/request';

interface ApiResult<T> {
  code: number;
  message: string;
  data: T;
}

export interface AiChatPayload {
  message: string;
}

export interface BackendUserProfile {
  id?: number;
  userId?: number;
  name?: string;
  grade?: string;
  major?: string;
  school?: string;
  targetRole?: string;
  targetCity?: string;
  currentStage?: string;
  skills?: string;
  weakSkills?: string;
  interests?: string;
  careerPreference?: string;
  profileSummary?: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface BackendCareerPlan {
  id?: number;
  userId?: number;
  currentStage?: string;
  currentLevel?: string;
  shortTermGoal?: string;
  threeMonthPlan?: string;
  oneYearPlan?: string;
  weeklyTasks?: string;
  roadmap?: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface BackendAbilityAnalysis {
  id?: number;
  userId?: number;
  targetRole?: string;
  matchScore?: number;
  strengths?: string;
  weaknesses?: string;
  priorities?: string;
  skillScores?: string;
  suggestions?: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface BackendResumeMaterial {
  id?: number;
  userId?: number;
  type?: string;
  title?: string;
  rawText?: string;
  optimizedText?: string;
  tags?: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface AiChatResponse {
  reply: string;
  profile?: BackendUserProfile | null;
  careerPlan?: BackendCareerPlan | null;
  abilityAnalysis?: BackendAbilityAnalysis | null;
  resumeMaterial?: BackendResumeMaterial | null;
  tasks?: string[];
}

export interface AiMessage {
  id: number;
  userId: number;
  role: 'user' | 'assistant';
  content: string;
  createdAt: string;
}

function unwrapResult<T>(result: ApiResult<T>) {
  if (result.code !== 200) {
    throw new Error(result.message || '请求失败');
  }

  return result.data;
}

export async function sendAiChatApi(payload: AiChatPayload) {
  const response = await request.post<ApiResult<AiChatResponse>>('/ai/chat', {
    message: payload.message,
  });

  return unwrapResult(response.data);
}

export async function getAiMessagesApi() {
  const response = await request.get<ApiResult<AiMessage[]>>('/ai/messages');

  return unwrapResult(response.data);
}
