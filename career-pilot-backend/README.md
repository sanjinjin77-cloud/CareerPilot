# CareerPilot Backend

CareerPilot 后端基于 FastAPI、SQLAlchemy、Pydantic Settings、JWT 和 MySQL 构建。

## 当前阶段

当前已完成项目初始化、基础骨架，以及第四阶段首批接口：

- FastAPI 应用入口
- CORS 配置
- 健康检查接口
- MVP 模块路由占位
- 数据库连接入口
- JWT 与密码加密工具
- 统一 AI 服务层占位
- 用户注册接口
- 用户登录接口
- 当前用户信息接口
- 用户画像读取、创建、更新接口
- 职业规划生成与列表接口
- 能力差距分析接口
- 简历素材 CRUD 接口
- JD 分析接口
- 模拟面试问题与评分接口

## 创建虚拟环境

```bash
python -m venv .venv
```

Windows PowerShell：

```bash
.\.venv\Scripts\Activate.ps1
```

## 安装依赖

```bash
pip install -r requirements.txt
```

## 配置环境变量

复制 `.env.example` 为 `.env`，并按本地 MySQL 配置修改：

```text
DATABASE_URL=mysql+pymysql://root:password@127.0.0.1:3306/career_pilot
SECRET_KEY=please-change-this-secret
```

启动前请先在 MySQL 中创建数据库：

```sql
CREATE DATABASE career_pilot DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

应用启动时会根据 SQLAlchemy 模型自动创建 MVP 阶段所需的 `users` 和 `user_profiles` 表。
现在也会自动创建 `career_plans`、`ability_reports`、`resume_materials`、`jd_analysis` 和 `interview_records` 表。

## 本地启动

```bash
uvicorn app.main:app --reload
```

默认访问地址：

```text
http://127.0.0.1:8000
```

接口文档：

```text
http://127.0.0.1:8000/docs
```

健康检查：

```text
GET http://127.0.0.1:8000/api/health
```

## 已完成接口

注册：

```text
POST /api/auth/register
```

请求体：

```json
{
  "username": "student",
  "email": "student@example.com",
  "password": "123456"
}
```

登录：

```text
POST /api/auth/login
```

请求体：

```json
{
  "account": "student@example.com",
  "password": "123456"
}
```

读取当前用户：

```text
GET /api/auth/me
Authorization: Bearer <token>
```

读取用户画像：

```text
GET /api/profile/me
Authorization: Bearer <token>
```

创建或更新用户画像：

```text
POST /api/profile/me
PUT /api/profile/me
Authorization: Bearer <token>
```

生成职业规划：

```text
POST /api/planning/generate
Authorization: Bearer <token>
```

能力差距分析：

```text
POST /api/ability/analyze
Authorization: Bearer <token>
```

简历素材：

```text
GET /api/resume/materials
POST /api/resume/materials
PUT /api/resume/materials/{material_id}
DELETE /api/resume/materials/{material_id}
Authorization: Bearer <token>
```

JD 分析：

```text
GET /api/jd/analysis
POST /api/jd/analyze
Authorization: Bearer <token>
```

模拟面试：

```text
GET /api/interview/records
POST /api/interview/question
POST /api/interview/answer
Authorization: Bearer <token>
```

## 后续开发顺序

1. 前端 service 接入真实后端接口
2. AI 服务真实模型接入
3. 错误处理与接口测试补充
4. 部署配置
