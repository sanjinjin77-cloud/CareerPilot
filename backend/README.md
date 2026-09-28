# CareerPilot Backend

这是 CareerPilot 当前唯一主后端，基于 Spring Boot 3、Java 17、MyBatis Plus 和 MySQL 构建。前端所有 `/api` 请求最终都进入本服务。

## 模块结构

```text
src/main/java/com/careerpilot/
├─ ai/             # OpenAI-compatible 大模型封装与本地 fallback
├─ common/         # 统一响应、异常处理
├─ config/         # CORS 等基础配置
├─ controller/     # REST API
├─ dto/            # 请求 DTO
├─ entity/         # 数据库实体
├─ mapper/         # MyBatis Plus Mapper
├─ service/        # 业务接口
├─ service/impl/   # 业务实现
└─ vo/             # 响应视图对象
```

## 本地启动

推荐从项目根目录统一启动：

```powershell
cd ..
npm run dev
```

仅调试后端时，可在本目录单独启动：

```powershell
mvn spring-boot:run
```

单独启动后端前，请确认 MySQL 已启动，并且环境变量或 `.env.local` 配置正确。统一脚本会自动读取 `backend/.env.local`，直接运行 Maven 时不会自动读取该文件，除非你在终端提前设置了环境变量。

## 数据库

默认连接：

```text
jdbc:mysql://127.0.0.1:3306/career_pilot
```

默认账号：

```text
MYSQL_USERNAME=root
MYSQL_PASSWORD=123456
```

`application.yml` 使用 `createDatabaseIfNotExist=true`，数据库不存在时会尝试自动创建。表结构来自：

```text
src/main/resources/schema.sql
```

本地开发默认：

```text
SQL_INIT_MODE=always
```

这样后端启动时会执行 schema 初始化，`continue-on-error=true` 会避免已存在表导致启动失败。

## AI 配置

本项目支持 OpenAI-compatible `/chat/completions` 接口。配置位置：

```text
backend/.env.local
```

示例：

```env
AI_ENABLED=true
AI_BASE_URL=https://api.openai.com/v1
AI_API_KEY=your-api-key
AI_MODEL=your-model-name
AI_TIMEOUT_SECONDS=60
```

未启用 AI、Key 为空、模型调用失败或返回格式不符合预期时，后端会自动使用本地 fallback 规则，并在响应字段中返回 `local_fallback`。成功使用大模型时，相关来源字段为 `llm`。

## 主要接口

基础与认证：

- `GET /api/health`
- `POST /api/auth/login`
- `POST /api/auth/register`
- `GET /api/auth/me`

用户画像与仪表盘：

- `GET /api/profile/me`
- `PUT /api/profile/me`
- `GET /api/dashboard/summary`

职业规划与岗位：

- `POST /api/planning/generate`
- `GET /api/career/job-match`
- `GET /api/jobs`
- `GET /api/jobs/{id}`
- `GET /api/target-offer/current`
- `POST /api/target-offer/{jobId}`

能力分析与学习计划：

- `POST /api/ability/analyze`
- `POST /api/learning/plan/generate`
- `GET /api/learning/plan/current`
- `GET /api/learning/tasks`
- `POST /api/learning/tasks/{taskId}/complete`
- `POST /api/learning/tasks/{taskId}/material`
- `POST /api/learning/interview-review`

简历与 JD：

- `GET /api/resume/materials`
- `POST /api/resume/materials`
- `PUT /api/resume/materials/{materialId}`
- `POST /api/resume/materials/{materialId}/optimize`
- `DELETE /api/resume/materials/{materialId}`
- `POST /api/jd/analyze`
- `POST /api/resume/customize`
- `GET /api/resume/documents`
- `GET /api/resume/documents/{documentId}`
- `PUT /api/resume/documents/{documentId}`
- `DELETE /api/resume/documents/{documentId}`

模拟面试与 AI 助手：

- `POST /api/interview/question`
- `POST /api/interview/answer`
- `GET /api/interview/records`
- `DELETE /api/interview/records/{recordId}`
- `POST /api/ai/chat`
- `GET /api/ai/messages`

## 验证

```powershell
mvn -q -DskipTests compile
```

健康检查：

```powershell
Invoke-WebRequest http://127.0.0.1:8081/api/health -UseBasicParsing
```
