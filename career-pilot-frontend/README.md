# CareerPilot Frontend

这是 CareerPilot 前端应用，基于 Vue 3、TypeScript、Vite、Pinia、Vue Router、Element Plus、Axios 和 ECharts 构建。

## 页面模块

- 登录与注册：`/login`、`/register`
- 首页仪表盘：`/`
- 用户画像：`/profile`
- 职业规划：`/planning`
- 学习计划：`/learning-plan`
- 岗位匹配：`/career/job-match`
- 岗位筛选：`/career/job-filter`
- JD 分析：`/career/jd-analysis`
- 能力分析：`/ability`
- 简历素材库：`/resume/materials`
- 简历模板：`/resume/templates`
- 模拟面试：`/interview`

## 后端连接

开发环境下，Vite 将所有 `/api` 请求代理到 Spring Boot：

```text
/api -> http://127.0.0.1:8081
```

接口封装入口：

```text
src/api/request.ts
src/api/auth.ts
src/api/profile.ts
src/api/dashboard.ts
src/api/career.ts
src/api/ai.ts
```

## 本地启动

推荐从项目根目录统一启动前后端：

```powershell
cd ..
npm run dev
```

只调试前端时，可以在本目录运行：

```powershell
npm install
npm run dev
```

前端访问地址：

```text
http://127.0.0.1:5174
```

## 构建与类型检查

```powershell
npm run type-check
npm run build
```

当前构建脚本使用 `vue-tsc --noEmit --incremental false`，只做类型检查，不向 `src` 目录生成 `.js` 镜像文件或 `tsconfig.tsbuildinfo` 缓存。

## Mock 与 fallback

项目目前以真实后端和 MySQL 为主。部分页面仍保留 mock/fallback 展示能力，用于后端接口异常时保证演示不完全中断。

集中位置：

```text
src/mock/careerPilotMock.ts
src/services/mockCareerService.ts
```

后续如要进一步产品化，可以逐步减少前端 mock，把 fallback 统一收口到后端服务层。
