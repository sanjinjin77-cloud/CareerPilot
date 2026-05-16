import type {
  AbilityReport,
  CareerPlanBlock,
  DashboardSummaryItem,
  FeatureEntry,
  InterviewRecord,
  JDAnalysis,
  ResumeMaterial,
  UserProfile,
} from '@/types';

export const mockProfile: UserProfile = {
  grade: '大二',
  educationLevel: '本科',
  schoolType: '普通本科',
  major: '软件工程',
  currentStage: '能力建设期',
  targetPosition: '前端开发工程师',
  targetCity: '杭州',
  targetIndustry: '互联网',
  languages: 'JavaScript, TypeScript, Python',
  frameworks: 'Vue 3, Element Plus, FastAPI',
  projects: '课程管理系统、个人博客、登录鉴权模块',
  competitions: '蓝桥杯省赛备赛、校级创新项目',
  internships: '暂无正式实习',
  englishLevel: 'CET-4',
  certificates: '普通话二甲、软件设计师备考中',
  preferences: ['技术岗', '高成长', '接受异地'],
};

export const dashboardSummary: DashboardSummaryItem[] = [
  {
    icon: 'Stage',
    tag: '成长档案',
    label: '当前成长阶段',
    value: '大二 · 能力建设期',
    description: '正在从专业基础过渡到项目积累。',
    progress: 46,
  },
  {
    icon: 'Goal',
    tag: '目标锁定',
    label: '目标岗位',
    value: '前端开发工程师',
    description: '优先关注 Vue、TypeScript 与工程化。',
    progress: 62,
  },
  {
    icon: 'Match',
    tag: '本周提升',
    label: '岗位匹配度',
    value: '72%',
    description: '距离实习投递还需补齐关键短板。',
    progress: 72,
  },
  {
    icon: 'Tasks',
    tag: '今日行动',
    label: '今日待完成任务',
    value: '3 项',
    description: '优化素材、复盘 JD、完成面试题。',
    progress: 35,
  },
];

export const careerPlanBlocks: CareerPlanBlock[] = [
  {
    title: '未来 3 个月',
    tasks: ['完成 TypeScript 基础与组合式 API 训练', '重构 1 个课程项目并补充 README', '沉淀 3 条可写入简历的项目素材'],
  },
  {
    title: '未来 1 年',
    tasks: ['完成前端工程化和性能优化专题', '参与 1 次比赛或开源协作', '投递并复盘 10 个实习岗位'],
  },
  {
    title: '每周任务',
    tasks: ['整理 1 条 STAR 项目表达', '完成 2 道岗位相关面试题', '复盘 1 个目标 JD 的关键词'],
  },
];

export const abilityReport: AbilityReport = {
  targetPosition: '前端开发工程师',
  matchScore: 72,
  strengths: ['Vue 基础', '页面开发能力', '课程项目经验'],
  weaknesses: ['TypeScript', '工程化', '性能优化', '面试表达'],
  suggestions: ['TypeScript > 项目重构 > 简历优化 > 模拟面试', '将课程管理系统补充为前后端联调项目', '准备 3 个项目难点的 STAR 表达'],
};

export const resumeMaterials: ResumeMaterial[] = [
  {
    id: 1,
    type: '项目经历',
    title: '课程管理系统登录模块',
    originalContent: '我做了一个登录页面，可以输入账号密码，然后跳转首页。',
    optimizedContent: '负责课程管理系统登录模块开发，实现账号密码输入、登录状态管理与页面路由跳转，提升系统认证流程完整性。',
    tags: ['Vue', '登录鉴权', '路由'],
  },
  {
    id: 2,
    type: '课程设计',
    title: '个人博客前端页面',
    originalContent: '做了博客首页和文章详情页。',
    optimizedContent: '完成个人博客首页、文章列表与详情页开发，封装通用文章卡片组件，提高页面复用性与维护效率。',
    tags: ['组件封装', '页面开发'],
  },
  {
    id: 3,
    type: '比赛经历',
    title: '蓝桥杯算法训练',
    originalContent: '刷了一些算法题，准备参加蓝桥杯。',
    optimizedContent: '围绕蓝桥杯竞赛完成基础算法训练，覆盖数组、字符串、动态规划等题型，提升代码实现与问题拆解能力。',
    tags: ['算法', '比赛'],
  },
];

export const jdAnalysis: JDAnalysis = {
  jobTitle: '前端开发实习生',
  companyName: '某互联网公司',
  jdContent: '岗位要求：熟悉 Vue、TypeScript、前端工程化，有良好的项目表达能力和团队协作意识。',
  keywords: ['Vue', 'TypeScript', '工程化', '项目表达'],
  requirements: ['掌握 Vue 3 基础开发', '了解 TypeScript 类型系统', '具备项目组件化与工程化意识', '能够清晰表达项目职责和结果'],
  matchScore: 72,
  recommendedMaterials: ['课程管理系统登录模块', '个人博客前端页面', '前后端联调项目'],
  suggestions: ['补充 TypeScript 使用细节', '突出组件拆分和状态管理', '用数据说明项目效果'],
};

export const interviewRecord: InterviewRecord = {
  jobTitle: '前端开发工程师',
  question: '请介绍你最能体现前端工程能力的一个项目，并说明你在其中解决了什么问题。',
  answer: '',
  scores: {
    technical: 70,
    project: 76,
    logic: 62,
    star: 58,
  },
  feedback: '回答中能说明项目背景，但建议补充技术选型、关键难点、个人职责和量化结果。',
};

export const featureEntries: FeatureEntry[] = [
  {
    icon: 'Profile',
    title: '用户画像采集',
    description: '记录年级、专业、目标岗位与能力基础，建立专属成长档案。',
    path: '/profile',
  },
  {
    icon: 'Plan',
    title: 'AI 职业规划',
    description: '根据个人情况生成大学阶段学习路线和求职规划。',
    path: '/planning',
  },
  {
    icon: 'Gap',
    title: '能力差距分析',
    description: '对比目标岗位要求，定位当前短板和提升优先级。',
    path: '/ability',
  },
  {
    icon: 'Resume',
    title: '简历素材库',
    description: '沉淀项目、比赛、课程设计和实习经历，为简历提前积累素材。',
    path: '/resume',
  },
  {
    icon: 'JD',
    title: 'JD 匹配优化',
    description: '根据岗位 JD 自动分析关键词，生成更匹配岗位的简历表达。',
    path: '/jd',
  },
  {
    icon: 'Mock',
    title: 'AI 模拟面试',
    description: '围绕目标岗位和简历项目进行真实追问训练，并生成复盘建议。',
    path: '/interview',
  },
];

export const aiSuggestion =
  '你当前目标岗位是前端开发工程师，建议本周重点补强 TypeScript 和项目工程化表达，并完善一个可写入简历的前后端联调项目。';
