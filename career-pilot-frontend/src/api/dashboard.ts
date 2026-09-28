import { request } from '@/api/request';
import type { DashboardSnapshot } from '@/types';

interface DashboardResponse {
  grade: string;
  current_stage: string;
  target_position: string;
  current_level: string;
  match_score: number;
  match_trend: number;
  learning_progress: number;
  total_tasks: number;
  completed_tasks: number;
  pending_tasks: number;
  resume_material_count: number;
  materialized_task_count: number;
  jd_analysis_count: number;
  interview_count: number;
  target_offer_title: string;
  target_offer_company: string;
  target_offer_match_score: number;
  final_resume_title: string;
  next_task_title: string;
  next_task_type: string;
  next_task_due_date: string;
  milestone_step: number;
  milestone_total: number;
  milestone_labels: string[];
  suggestion_title: string;
  suggestion: string;
  suggestion_path: string;
  data_source: 'database';
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

export async function getDashboardSummaryApi(): Promise<DashboardSnapshot> {
  const response = await request.get<DashboardResponse>('/dashboard/summary');
  const data = response.data;
  return {
    grade: decodeMaybeMojibake(data.grade),
    currentStage: decodeMaybeMojibake(data.current_stage),
    targetPosition: decodeMaybeMojibake(data.target_position),
    currentLevel: decodeMaybeMojibake(data.current_level),
    matchScore: data.match_score,
    matchTrend: data.match_trend,
    learningProgress: data.learning_progress,
    totalTasks: data.total_tasks,
    completedTasks: data.completed_tasks,
    pendingTasks: data.pending_tasks,
    resumeMaterialCount: data.resume_material_count,
    materializedTaskCount: data.materialized_task_count,
    jdAnalysisCount: data.jd_analysis_count,
    interviewCount: data.interview_count,
    targetOfferTitle: decodeMaybeMojibake(data.target_offer_title),
    targetOfferCompany: decodeMaybeMojibake(data.target_offer_company),
    targetOfferMatchScore: data.target_offer_match_score,
    finalResumeTitle: decodeMaybeMojibake(data.final_resume_title),
    nextTaskTitle: decodeMaybeMojibake(data.next_task_title),
    nextTaskType: data.next_task_type,
    nextTaskDueDate: data.next_task_due_date,
    milestoneStep: data.milestone_step,
    milestoneTotal: data.milestone_total,
    milestoneLabels: decodeList(data.milestone_labels),
    suggestionTitle: decodeMaybeMojibake(data.suggestion_title),
    suggestion: decodeMaybeMojibake(data.suggestion),
    suggestionPath: data.suggestion_path,
    dataSource: data.data_source,
  };
}
