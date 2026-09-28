# CareerPilot

CareerPilot 是一个面向大学生的 AI 职业成长与求职陪跑平台，围绕“用户画像 -> 职业规划 -> 能力差距分析 -> 简历素材沉淀 -> JD 匹配与简历优化 -> 模拟面试 -> 学习计划”的闭环，帮助学生把职业探索、求职准备和能力提升串成一条真实数据流。

## 当前完成度

当前项目已进入 MVP 收口阶段，完成度约为 98%。

- 已完成单后端改造：后端统一为 Spring Boot，旧 FastAPI 不再作为运行链路。
- 已接入 MySQL：用户画像、职业规划、能力分析、简历素材、JD 分析、模拟面试、学习计划等核心数据进入数据库。
- 已接入 OpenAI-compatible 大模型接口：未配置或调用失败时保留本地 fallback，保证演示链路不断。
- 已完成统一开发启动脚本：根目录一条命令启动前端和后端。
- 已清理前端源码目录中的编译副本和构建缓存，项目结构更清晰。
- 已补充鉴权、密码、用户上下文的后端最小回归测试，并增加前端契约验证脚本。
- 已收口主要页面的 mock/fallback 提示，接口失败时会明确显示演示数据或错误状态。

剩余主要工作：在 MySQL 与后端服务在线后跑完整业务闭环，并整理答辩/演示材料。

## 项目结构

```text
CareerPilot/
├─ backend/                    # Spring Boot 3 + MyBatis Plus + MySQL 后端
├─ career-pilot-frontend/      # Vue 3 + TypeScript + Vite + Element Plus 前端
├─ scripts/                    # 统一启动、停止、状态检查脚本
├─ docs/                       # 数据库设计、启动说明等项目文档
├─ AI_CareerPilot_Project_Guide.md
├─ package.json                # 根目录统一开发命令
└─ README.md
```

## 功能闭环

1. 登录/注册：进入系统并初始化用户上下文。
2. 用户画像：保存年级、专业、技能、目标方向、经历等基础信息。
3. 职业规划：根据画像生成阶段性职业目标和行动建议。
4. 能力分析：评估技能、项目、简历、面试等能力差距。
5. 简历素材库：沉淀项目经历、实习经历、技能亮点和可优化表达。
6. JD 分析：分析岗位要求，匹配素材并给出简历优化方向。
7. 简历定制：基于目标岗位生成定制化简历草稿。
8. 模拟面试：生成面试题、记录回答、给出评分和改进建议。
9. 学习计划：把能力差距和面试反馈转化为任务化学习路径。

## 技术栈

- 前端：Vue 3、TypeScript、Vite、Pinia、Vue Router、Element Plus、Axios、ECharts
- 后端：Spring Boot 3、Java 17、MyBatis Plus、MySQL
- AI：OpenAI-compatible `/chat/completions` API
- 开发脚本：PowerShell + npm scripts

## 本地运行

前置条件：

- JDK 17+
- Maven
- Node.js 18+
- MySQL 已启动并监听 `3306`
- 默认数据库账号：`root`
- 默认数据库密码：`123456`

在项目根目录执行：

```powershell
npm run dev
```

启动并打开浏览器：

```powershell
npm run dev:open
```

查看状态：

```powershell
npm run dev:status
```

停止由统一脚本启动的服务：

```powershell
npm run dev:stop
```

访问地址：

- Web 入口：http://127.0.0.1:5174
- 后端健康检查：http://127.0.0.1:8081/api/health
- 运行日志：`.dev/logs/`

更详细的启动、MySQL、Maven、端口占用和常见问题说明见 [docs/STARTUP.md](docs/STARTUP.md)。

## 配置说明

后端读取 `backend/.env.local` 中的本地配置。该文件不会提交到 Git，适合保存数据库密码和 AI Key。

可以参考 `backend/.env.example`：

```env
AI_ENABLED=false
AI_BASE_URL=https://api.openai.com/v1
AI_API_KEY=
AI_MODEL=
AI_TIMEOUT_SECONDS=60
AI_QUOTA_ENABLED=true
AI_DAILY_LIMIT=20

MYSQL_USERNAME=root
MYSQL_PASSWORD=123456
SQL_INIT_MODE=always
SERVER_PORT=8081
```

启用真实大模型时，将 `AI_ENABLED` 改为 `true`，并填写 `AI_API_KEY` 和 `AI_MODEL`。

## 开发约定

- 日常开发优先使用根目录 `npm run dev`，不要分别手动启动前后端，避免端口和环境变量不一致。
- 后端配置集中在 `backend/src/main/resources/application.yml`，密钥只写入 `backend/.env.local`。
- 前端所有 `/api` 请求通过 Vite 代理到 Spring Boot：`http://127.0.0.1:8081`。
- `backend/target/`、`career-pilot-frontend/dist/`、`node_modules/`、`.dev/`、`.vscode/` 都是本地生成或个人配置，不应提交。
- 前端源码以 `.ts` 和 `.vue` 为准，`src` 目录不保留编译生成的 `.js` 镜像文件。

## 验证命令

前端构建：

```powershell
cd career-pilot-frontend
npm run build
```

后端编译：

```powershell
cd backend
mvn -q -DskipTests compile
```

后端测试：

```powershell
cd backend
mvn -q test
```

前端契约检查：

```powershell
cd career-pilot-frontend
npm run verify:contracts
```

统一服务状态：

```powershell
npm run dev:status
```
