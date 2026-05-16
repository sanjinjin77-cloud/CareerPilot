CREATE DATABASE IF NOT EXISTS career_pilot
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE career_pilot;

CREATE TABLE IF NOT EXISTS user_profile (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL DEFAULT 1,
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

ALTER TABLE user_profile ADD COLUMN IF NOT EXISTS education_level VARCHAR(64) DEFAULT '' AFTER grade;
ALTER TABLE user_profile ADD COLUMN IF NOT EXISTS expected_salary VARCHAR(64) DEFAULT '' AFTER target_city;
ALTER TABLE user_profile ADD COLUMN IF NOT EXISTS industry_preference VARCHAR(128) DEFAULT '' AFTER expected_salary;
ALTER TABLE user_profile ADD COLUMN IF NOT EXISTS graduation_time DATE AFTER industry_preference;
ALTER TABLE user_profile ADD COLUMN IF NOT EXISTS weekly_learning_hours INT DEFAULT 0 AFTER graduation_time;
ALTER TABLE user_profile ADD COLUMN IF NOT EXISTS job_search_status VARCHAR(64) DEFAULT '' AFTER weekly_learning_hours;
ALTER TABLE user_profile ADD COLUMN IF NOT EXISTS job_preference VARCHAR(128) DEFAULT '' AFTER job_search_status;
ALTER TABLE user_profile ADD COLUMN IF NOT EXISTS project_experiences TEXT AFTER weak_skills;
ALTER TABLE user_profile ADD COLUMN IF NOT EXISTS competition_experiences TEXT AFTER project_experiences;
ALTER TABLE user_profile ADD COLUMN IF NOT EXISTS internship_experiences TEXT AFTER competition_experiences;
ALTER TABLE user_profile ADD COLUMN IF NOT EXISTS certificates TEXT AFTER internship_experiences;

CREATE TABLE IF NOT EXISTS chat_message (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL DEFAULT 1,
  role VARCHAR(32) NOT NULL,
  content TEXT NOT NULL,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_chat_message_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS career_plan (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL DEFAULT 1,
  current_stage VARCHAR(128) DEFAULT '',
  current_level VARCHAR(128) DEFAULT '',
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
  user_id BIGINT NOT NULL DEFAULT 1,
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

CREATE TABLE IF NOT EXISTS learning_plan (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL DEFAULT 1,
  target_offer_id BIGINT,
  plan_source VARCHAR(64) DEFAULT 'ai',
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
  user_id BIGINT NOT NULL DEFAULT 1,
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

CREATE TABLE IF NOT EXISTS ability_analysis (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL DEFAULT 1,
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

CREATE TABLE IF NOT EXISTS resume_material (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL DEFAULT 1,
  type VARCHAR(64) DEFAULT '',
  title VARCHAR(255) DEFAULT '',
  raw_text TEXT,
  optimized_text TEXT,
  tags TEXT,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_resume_material_user_id (user_id)
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
  user_id BIGINT NOT NULL DEFAULT 1,
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
  user_id BIGINT NOT NULL DEFAULT 1,
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
  user_id BIGINT NOT NULL DEFAULT 1,
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
('前端开发实习生', '杭州云启科技有限公司', '前端', '杭州', 180, 260, '本科及以上', '在校生/应届生', '实习', 'Vue,TypeScript,JavaScript,Element Plus', '参与 Web 端业务页面开发、组件封装和接口联调。', '熟悉 Vue 或 React，了解 TypeScript，有课程项目或个人项目经验优先。', '互联网', '现场', NOW(), 'local_seed'),
('Java 后端开发实习生', '上海数桥信息技术有限公司', '后端', '上海', 200, 300, '本科及以上', '在校生/应届生', '实习', 'Java,Spring Boot,MySQL,Redis', '参与后端接口开发、数据库设计和业务模块迭代。', '掌握 Java 基础，了解 Spring Boot 和 MySQL，有接口开发项目经验优先。', '软件服务', '现场', NOW(), 'local_seed'),
('测试开发实习生', '深圳星航智能科技有限公司', '测试', '深圳', 160, 240, '本科及以上', '在校生/应届生', '实习', 'Python,接口测试,自动化测试,MySQL', '参与 Web/移动端功能测试、接口测试和自动化脚本维护。', '了解软件测试流程，具备 Python 或 Java 基础，逻辑清晰。', '智能硬件', '现场', NOW(), 'local_seed'),
('数据分析实习生', '北京知数咨询有限公司', '数据', '北京', 150, 230, '本科及以上', '在校生/应届生', '实习', 'SQL,Python,Excel,Tableau', '参与业务数据清洗、指标分析和可视化报表制作。', '熟悉 SQL 和 Excel，了解 Python 数据分析，有数据分析项目优先。', '咨询服务', '混合', NOW(), 'local_seed'),
('AI 产品助理', '广州灵感互动科技有限公司', '产品', '广州', 150, 220, '本科及以上', '在校生/应届生', '实习', 'Axure,用户研究,AI产品,需求分析', '协助 AI 产品需求调研、竞品分析和原型设计。', '具备产品思维，了解大模型应用场景，有原型或需求文档作品优先。', '互联网', '现场', NOW(), 'local_seed'),
('Web 全栈开发实习生', '南京青禾软件有限公司', '全栈', '南京', 180, 280, '本科及以上', '在校生/应届生', '实习', 'Vue,Java,Spring Boot,MySQL', '参与前后端功能开发、接口联调和项目部署。', '了解前端框架和后端接口开发，能独立完成小型模块。', '软件服务', '现场', NOW(), 'local_seed'),
('运维开发实习生', '成都云栈科技有限公司', '运维', '成都', 160, 240, '本科及以上', '在校生/应届生', '实习', 'Linux,Shell,Python,Docker', '参与自动化运维脚本、监控告警和部署流程优化。', '熟悉 Linux 基础命令，了解 Docker 或脚本开发。', '云计算', '现场', NOW(), 'local_seed'),
('算法工程实习生', '北京启明星图智能有限公司', 'AI', '北京', 250, 400, '本科及以上', '在校生/应届生', '实习', 'Python,机器学习,PyTorch,算法', '参与模型训练、数据处理和算法实验复现。', '具备 Python 和机器学习基础，有算法竞赛或模型项目经验优先。', '人工智能', '现场', NOW(), 'local_seed'),
('前端工程师校招', '苏州微链网络科技有限公司', '前端', '苏州', 9000, 14000, '本科及以上', '应届生', '校招', 'React,TypeScript,Webpack,JavaScript', '负责中后台系统和用户端页面开发。', '熟悉现代前端工程化，具备组件化开发和性能优化意识。', '互联网', '现场', NOW(), 'local_seed'),
('Java 开发工程师校招', '杭州桐云数字科技有限公司', '后端', '杭州', 10000, 16000, '本科及以上', '应届生', '校招', 'Java,Spring Boot,MySQL,Redis,消息队列', '负责业务系统后端模块设计、开发和维护。', 'Java 基础扎实，理解数据库、缓存和常见 Web 开发流程。', '互联网', '现场', NOW(), 'local_seed'),
('产品经理校招', '上海向量在线教育科技有限公司', '产品', '上海', 8000, 13000, '本科及以上', '应届生', '校招', '需求分析,原型设计,数据分析,教育产品', '负责教育产品需求分析、原型设计和版本迭代跟进。', '表达清晰，具备用户视角和数据意识，有产品实习或作品优先。', '教育科技', '混合', NOW(), 'local_seed'),
('数据开发实习生', '武汉湖畔数据科技有限公司', '数据', '武汉', 160, 260, '本科及以上', '在校生/应届生', '实习', 'SQL,Python,ETL,数据仓库', '参与数据同步、指标开发和数据质量校验。', '熟悉 SQL，了解 Python 或数据仓库基础。', '大数据', '现场', NOW(), 'local_seed');

INSERT IGNORE INTO resume_template
(template_name, template_category, suitable_roles, preview_url, file_url, source)
VALUES
('技术岗简洁单页模板', '技术岗', '前端,后端,测试,全栈,运维', '', '', 'local_seed'),
('产品运营通用模板', '产品岗', '产品,运营,AI产品助理', '', '', 'local_seed'),
('数据分析项目型模板', '数据岗', '数据分析,数据开发,算法', '', '', 'local_seed');
