# CareerPilot Frontend

CareerPilot 前端基于 Vue 3、TypeScript、Vite、Pinia、Vue Router、Element Plus、Axios 和 ECharts 构建。

## 当前阶段

已完成第一阶段项目初始化和第二阶段基础页面与路由：

- Vite + Vue 3 + TypeScript 项目入口
- Element Plus 全局接入
- Pinia 状态管理入口
- Vue Router 路由入口
- Axios 请求封装
- 主布局与仪表盘占位页面
- 登录页与注册页
- 用户画像页
- AI 职业规划页
- 能力差距分析页
- 简历素材库页
- JD 匹配页
- AI 模拟面试页
- 首页 UI 视觉重构：顶部导航、Hero Banner、数据总览、通关路线、功能入口和 AI 建议模块

当前登录注册使用本地状态模拟，第三阶段会把 mock 数据集中管理，第四阶段再替换为真实后端接口。

第三阶段已开始：页面数据已集中放在 `src/mock/careerPilotMock.ts`，当前首页、用户画像、职业规划、能力分析、简历素材、JD 匹配和模拟面试页面均从该文件读取展示数据。

mock 交互逻辑已集中放在 `src/services/mockCareerService.ts`，页面通过 service 调用 mock 行为，后续接入后端 API 时优先替换 service 实现。

当前已开始接入真实后端接口：

- 登录：`POST /api/auth/login`
- 注册：`POST /api/auth/register`
- 当前用户：`GET /api/auth/me`
- 用户画像读取：`GET /api/profile/me`
- 用户画像保存：`PUT /api/profile/me`

当前已完成部分 mock 交互：

- 用户画像：保存画像、动态档案完整度、基于画像生成规划预览
- 职业规划：重新生成阶段规划和每周任务
- 简历素材库：AI 优化表达、新增素材到本地列表
- JD 匹配：分析 JD、生成简历优化建议
- 模拟面试：提交回答生成 mock 评分反馈、切换面试问题

## 首页结构

首页已从后台管理式左侧栏改为产品首页结构：

1. 顶部导航 Header
2. Hero Banner：从入学到 Offer，AI 陪你一路通关
3. Dashboard Summary：当前阶段、目标岗位、岗位匹配度、今日任务
4. Growth Roadmap：职业认知到 Offer 冲刺的通关路径
5. Feature Grid：六个 MVP 核心功能入口
6. AI Suggestion：今日 AI 建议

## 安装依赖

```bash
npm install
```

## 本地启动

```bash
npm run dev
```

默认访问地址：

```text
http://localhost:5173
```

## 构建检查

```bash
npm run build
```

## 后续开发顺序

1. 职业规划接口联调
2. 能力分析接口联调
3. 简历素材接口联调
4. JD 分析接口联调
5. 模拟面试接口联调
