import { request } from '@/api/request';
import type { UserProfile } from '@/types';

interface BackendProfile {
  id: number;
  user_id: number;
  grade: string;
  education_level: string;
  school_type: string;
  major: string;
  current_stage: string;
  target_position: string;
  target_city: string;
  target_industry: string;
  languages: string;
  frameworks: string;
  projects: string;
  competitions: string;
  internships: string;
  english_level: string;
  certificates: string;
  preferences: string[];
}

function toBackendProfile(profile: UserProfile) {
  return {
    grade: profile.grade,
    education_level: profile.educationLevel,
    school_type: profile.schoolType,
    major: profile.major,
    current_stage: profile.currentStage,
    target_position: profile.targetPosition,
    target_city: profile.targetCity,
    target_industry: profile.targetIndustry,
    languages: profile.languages,
    frameworks: profile.frameworks,
    projects: profile.projects,
    competitions: profile.competitions,
    internships: profile.internships,
    english_level: profile.englishLevel,
    certificates: profile.certificates,
    preferences: profile.preferences,
  };
}

export function toFrontendProfile(profile: BackendProfile): UserProfile {
  return {
    grade: profile.grade,
    educationLevel: profile.education_level,
    schoolType: profile.school_type,
    major: profile.major,
    currentStage: profile.current_stage,
    targetPosition: profile.target_position,
    targetCity: profile.target_city,
    targetIndustry: profile.target_industry,
    languages: profile.languages,
    frameworks: profile.frameworks,
    projects: profile.projects,
    competitions: profile.competitions,
    internships: profile.internships,
    englishLevel: profile.english_level,
    certificates: profile.certificates,
    preferences: profile.preferences,
  };
}

export async function getProfileApi() {
  const response = await request.get<BackendProfile>('/profile/me');
  return toFrontendProfile(response.data);
}

export async function saveProfileApi(profile: UserProfile) {
  const response = await request.put<BackendProfile>('/profile/me', toBackendProfile(profile));
  return toFrontendProfile(response.data);
}
