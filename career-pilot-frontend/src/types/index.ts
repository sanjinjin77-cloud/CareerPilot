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

export interface CareerPlanBlock {
  title: string;
  tasks: string[];
}

export interface AbilityReport {
  targetPosition: string;
  matchScore: number;
  strengths: string[];
  weaknesses: string[];
  suggestions: string[];
}

export interface ResumeMaterial {
  id: number;
  type: string;
  title: string;
  originalContent: string;
  optimizedContent: string;
  tags: string[];
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
}

export interface InterviewRecord {
  jobTitle: string;
  question: string;
  answer: string;
  scores: {
    technical: number;
    project: number;
    logic: number;
    star: number;
  };
  feedback: string;
}

export interface FeatureEntry {
  icon: string;
  title: string;
  description: string;
  path: string;
}
