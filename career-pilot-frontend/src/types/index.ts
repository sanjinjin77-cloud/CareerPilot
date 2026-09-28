export interface ApiResponse<T> {
  data: T;
  message: string;
  success: boolean;
}

export interface UserProfile {
  grade: string;
  educationLevel: string;
  schoolType: string;
  major: string;
  currentStage: string;
  targetPosition: string;
  targetCity: string;
  targetIndustry: string;
  languages: string;
  frameworks: string;
  projects: string;
  competitions: string;
  internships: string;
  englishLevel: string;
  certificates: string;
  preferences: string[];
}

export interface DashboardSummaryItem {
  icon: string;
  tag: string;
  label: string;
  value: string;
  description: string;
  progress: number;
}

export interface DashboardSnapshot {
  grade: string;
  currentStage: string;
  targetPosition: string;
  currentLevel: string;
  matchScore: number;
  matchTrend: number;
  learningProgress: number;
  totalTasks: number;
  completedTasks: number;
  pendingTasks: number;
  resumeMaterialCount: number;
  materializedTaskCount: number;
  jdAnalysisCount: number;
  interviewCount: number;
  targetOfferTitle: string;
  targetOfferCompany: string;
  targetOfferMatchScore: number;
  finalResumeTitle: string;
  nextTaskTitle: string;
  nextTaskType: string;
  nextTaskDueDate: string;
  milestoneStep: number;
  milestoneTotal: number;
  milestoneLabels: string[];
  suggestionTitle: string;
  suggestion: string;
  suggestionPath: string;
  dataSource: 'database' | 'mock';
}

export interface CareerPlanBlock {
  title: string;
  tasks: string[];
}

export interface LearningPlan {
  id: number;
  currentStage: string;
  totalPeriod: string;
  weeklyHours: number;
  goalSummary: string;
  stageTasks: string;
  skillTasks: string;
  projectTasks: string;
  resumeTasks: string;
  interviewTasks: string;
  progress: number;
  generationSource: 'llm' | 'local_fallback';
  model?: string;
}

export interface LearningTask {
  id: number;
  planId: number;
  taskType: string;
  title: string;
  description: string;
  priority: string;
  status: string;
  dueDate: string;
  completedAt?: string;
}

export interface AbilityReport {
  targetPosition: string;
  matchScore: number;
  strengths: string[];
  weaknesses: string[];
  suggestions: string[];
  summary: string;
  skillScores: Record<string, number>;
  analysisSource: 'llm' | 'local_fallback';
  model?: string;
}

export interface ResumeMaterial {
  id: number;
  type: string;
  title: string;
  originalContent: string;
  optimizedContent: string;
  tags: string[];
  sourceTaskId?: number;
  sourceType?: string;
  optimizationSource?: 'llm' | 'local_fallback' | '';
  model?: string;
}

export interface ResumeDocument {
  id: number;
  title: string;
  sourceType: string;
  content: string;
  optimizedContent: string;
  status: string;
  matchedMaterials: string[];
  keywords: string[];
  generationSource: 'llm' | 'local_fallback';
  model?: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface JDAnalysis {
  jobTitle: string;
  companyName: string;
  jdContent: string;
  keywords: string[];
  requirements: string[];
  matchScore: number;
  recommendedMaterials: string[];
  suggestions: string[];
  analysisSource: 'llm' | 'local_fallback';
  model?: string;
}

export interface InterviewRecord {
  id?: number;
  jobTitle: string;
  resumeDocumentId?: number;
  resumeTitle?: string;
  question: string;
  answer: string;
  scores: {
    technical: number;
    project: number;
    logic: number;
    star: number;
  };
  feedback: string;
  evaluationSource: 'llm' | 'local_fallback' | 'history';
  model?: string;
  createdAt?: string;
}

export interface InterviewQuestion {
  jobTitle: string;
  resumeDocumentId?: number;
  resumeTitle: string;
  question: string;
  keywords: string[];
  generationSource: 'llm' | 'local_fallback';
  model?: string;
}

export interface InterviewImprovementPlan {
  jobTitle: string;
  averageScore: number;
  focusAreas: string[];
  summary: string;
  tasks: LearningTask[];
}

export interface JobPosition {
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

export interface TargetOffer {
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

export interface JobMatch {
  job: JobPosition;
  matchScore: number;
  matchReasons: string[];
}

export interface FeatureEntry {
  icon: string;
  title: string;
  description: string;
  path: string;
}
