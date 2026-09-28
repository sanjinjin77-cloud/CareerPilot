CREATE DATABASE IF NOT EXISTS career_pilot
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE career_pilot;

CREATE TABLE IF NOT EXISTS user_account (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(64) NOT NULL,
  email VARCHAR(128) NOT NULL,
  password_hash VARCHAR(128) NOT NULL,
  salt VARCHAR(64) NOT NULL,
  status VARCHAR(32) DEFAULT 'active',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_user_account_username (username),
  UNIQUE KEY uk_user_account_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT IGNORE INTO user_account
(username, email, password_hash, salt, status)
VALUES
('student', 'student@example.com', '7TqAvjI3IeQER1wtBa71vLqXBE5o84VoeE6rNIHfae0=', 'career-pilot-demo-salt', 'active');

CREATE TABLE IF NOT EXISTS user_profile (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  name VARCHAR(64) DEFAULT '',
  grade VARCHAR(32) DEFAULT '',
  education_level VARCHAR(64) DEFAULT '',
  major VARCHAR(128) DEFAULT '',
  school VARCHAR(128) DEFAULT '',
  target_role VARCHAR(128) DEFAULT '',
  target_city VARCHAR(128) DEFAULT '',
  expected_salary VARCHAR(64) DEFAULT '',
  industry_preference VARCHAR(128) DEFAULT '',
  graduation_time DATE,
  weekly_learning_hours INT DEFAULT 0,
  job_search_status VARCHAR(64) DEFAULT '',
  job_preference VARCHAR(128) DEFAULT '',
  current_stage VARCHAR(128) DEFAULT '',
  skills TEXT,
  weak_skills TEXT,
  project_experiences TEXT,
  competition_experiences TEXT,
  internship_experiences TEXT,
  certificates TEXT,
  interests TEXT,
  career_preference TEXT,
  profile_summary TEXT,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_user_profile_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS chat_message (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  role VARCHAR(32) NOT NULL,
  content TEXT NOT NULL,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_chat_message_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS career_plan (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  current_stage VARCHAR(128) DEFAULT '',
  current_level VARCHAR(255) DEFAULT '',
  short_term_goal TEXT,
  three_month_plan TEXT,
  one_year_plan TEXT,
  weekly_tasks TEXT,
  roadmap TEXT,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_career_plan_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS job_position (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  job_title VARCHAR(128) NOT NULL,
  company_name VARCHAR(128) NOT NULL,
  job_category VARCHAR(64) DEFAULT '',
  city VARCHAR(64) DEFAULT '',
  salary_min INT DEFAULT 0,
  salary_max INT DEFAULT 0,
  education_requirement VARCHAR(64) DEFAULT '',
  experience_requirement VARCHAR(128) DEFAULT '',
  job_type VARCHAR(64) DEFAULT '',
  tech_stack TEXT,
  job_description TEXT,
  job_requirement TEXT,
  company_type VARCHAR(64) DEFAULT '',
  work_mode VARCHAR(64) DEFAULT '',
  publish_time DATETIME,
  source VARCHAR(128) DEFAULT 'local_seed',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_job_position_title_company (job_title, company_name),
  INDEX idx_job_position_category (job_category),
  INDEX idx_job_position_city (city),
  INDEX idx_job_position_job_type (job_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS target_offer (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  job_id BIGINT,
  job_title VARCHAR(128) DEFAULT '',
  company_name VARCHAR(128) DEFAULT '',
  target_role VARCHAR(128) DEFAULT '',
  target_city VARCHAR(64) DEFAULT '',
  target_salary VARCHAR(64) DEFAULT '',
  required_skills TEXT,
  job_requirements TEXT,
  match_score INT DEFAULT 0,
  status VARCHAR(64) DEFAULT 'active',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_target_offer_user_id (user_id),
  INDEX idx_target_offer_job_id (job_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS ability_analysis (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  target_role VARCHAR(128) DEFAULT '',
  match_score INT DEFAULT 0,
  strengths TEXT,
  weaknesses TEXT,
  priorities TEXT,
  skill_scores TEXT,
  suggestions TEXT,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_ability_analysis_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS learning_plan (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  target_offer_id BIGINT,
  plan_source VARCHAR(64) DEFAULT 'career_workflow',
  status VARCHAR(64) DEFAULT 'draft',
  current_stage VARCHAR(128) DEFAULT '',
  total_period VARCHAR(64) DEFAULT '',
  start_date DATE,
  end_date DATE,
  weekly_hours INT DEFAULT 0,
  goal_summary TEXT,
  stage_tasks TEXT,
  skill_tasks TEXT,
  project_tasks TEXT,
  resume_tasks TEXT,
  interview_tasks TEXT,
  progress INT DEFAULT 0,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_learning_plan_user_id (user_id),
  INDEX idx_learning_plan_target_offer_id (target_offer_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS learning_task (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  plan_id BIGINT,
  target_offer_id BIGINT,
  task_type VARCHAR(64) DEFAULT '',
  title VARCHAR(255) DEFAULT '',
  description TEXT,
  priority VARCHAR(32) DEFAULT 'medium',
  status VARCHAR(32) DEFAULT 'todo',
  due_date DATE,
  completed_at DATETIME,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_learning_task_user_id (user_id),
  INDEX idx_learning_task_plan_id (plan_id),
  INDEX idx_learning_task_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS resume_material (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  type VARCHAR(64) DEFAULT '',
  title VARCHAR(255) DEFAULT '',
  raw_text TEXT,
  optimized_text TEXT,
  tags TEXT,
  source_task_id BIGINT,
  source_type VARCHAR(64) DEFAULT '',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_resume_material_user_id (user_id),
  INDEX idx_resume_material_source_task_id (source_task_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS resume_template (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  template_name VARCHAR(128) NOT NULL,
  template_category VARCHAR(64) DEFAULT '',
  suitable_roles TEXT,
  preview_url VARCHAR(512) DEFAULT '',
  file_url VARCHAR(512) DEFAULT '',
  source VARCHAR(128) DEFAULT 'local',
  is_enabled TINYINT DEFAULT 1,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_resume_template_name (template_name),
  INDEX idx_resume_template_category (template_category)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS resume_document (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  target_offer_id BIGINT,
  template_id BIGINT,
  title VARCHAR(255) DEFAULT '',
  source_type VARCHAR(64) DEFAULT 'generated',
  original_file_url VARCHAR(512) DEFAULT '',
  content TEXT,
  optimized_content TEXT,
  status VARCHAR(64) DEFAULT 'draft',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_resume_document_user_id (user_id),
  INDEX idx_resume_document_target_offer_id (target_offer_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS jd_analysis (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  jd_text TEXT,
  keywords TEXT,
  match_score INT DEFAULT 0,
  recommended_materials TEXT,
  suggestions TEXT,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_jd_analysis_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS interview_record (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  interview_type VARCHAR(64) DEFAULT '',
  question TEXT,
  answer TEXT,
  score INT DEFAULT 0,
  feedback TEXT,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_interview_record_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT IGNORE INTO job_position
(job_title, company_name, job_category, city, salary_min, salary_max, education_requirement, experience_requirement, job_type, tech_stack, job_description, job_requirement, company_type, work_mode, publish_time, source)
VALUES
('Frontend Intern', 'Hangzhou CloudStart Tech', 'frontend', 'Hangzhou', 180, 260, 'Bachelor+', 'Student', 'internship', 'Vue,TypeScript,JavaScript,Element Plus', 'Build web pages, reusable components, and API integrations.', 'Familiar with Vue or React, basic TypeScript, and at least one course or personal project.', 'internet', 'onsite', NOW(), 'local_seed'),
('Java Backend Intern', 'Shanghai DataBridge', 'backend', 'Shanghai', 200, 300, 'Bachelor+', 'Student', 'internship', 'Java,Spring Boot,MySQL,Redis', 'Build backend APIs, database models, and business modules.', 'Solid Java basics, Spring Boot and MySQL project experience preferred.', 'software', 'onsite', NOW(), 'local_seed'),
('AI Product Assistant', 'Guangzhou InspireAI', 'product', 'Guangzhou', 150, 220, 'Bachelor+', 'Student', 'internship', 'Axure,User Research,AI Product,Requirement Analysis', 'Assist AI product research, competitor analysis, and prototype design.', 'Product mindset, clear writing, and understanding of LLM application scenarios.', 'internet', 'hybrid', NOW(), 'local_seed');

INSERT IGNORE INTO resume_template
(template_name, template_category, suitable_roles, preview_url, file_url, source)
VALUES
('Technical One Page Resume', 'technical', 'frontend,backend,testing,fullstack', '', '', 'local_seed'),
('Product General Resume', 'product', 'product,operation,AI product assistant', '', '', 'local_seed'),
('Data Project Resume', 'data', 'data analyst,data engineer,algorithm', '', '', 'local_seed');
