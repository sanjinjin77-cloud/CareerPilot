# 鉴权、AI 稳定性与 mock/fallback 改造计划

本文档记录增强阶段的现状审计、风险点和后续实施顺序。目标是把项目从可演示 MVP 进一步推进到更接近真实产品的版本。

## 当前增强阶段进度

当前完成度：91%

已完成：

- 梳理当前登录、注册、token、前端请求头、路由守卫实现。
- 梳理后端当前用户识别方式和核心数据的 `user_id` 隔离情况。
- 梳理 AI fallback 与前端 mock 的主要遗留位置。
- 后端 token 已从可伪造的 `cp_user_{userId}_{random}` 改为 HMAC-SHA256 签名 JWT。
- 未登录、token 无效、token 过期等认证错误已返回 HTTP 401。
- 前端已增加 401 自动清理本地 token 并跳转登录页的基础处理。
- 密码存储已改为 BCrypt，新注册用户直接使用 BCrypt。
- 旧 SHA-256+salt 用户可继续登录，并会在首次成功登录后自动迁移为 BCrypt。
- 前端登录态已改为“token + /auth/me 校验”模式，刷新页面后会先恢复当前用户再进入受保护页面。
- 前端路由守卫已支持异步登录态恢复，token 无效时会清理并回到登录页。
- 前端路由标题中的历史乱码已修复。
- 核心服务层已清理默认 1 号用户兜底，缺少合法用户 ID 时会返回未登录错误。
- AI Chat 已改为只信任 JWT 中的当前用户，不再接受前端传入的 `userId`。
- 前端全局 AI 助手已移除硬编码 `userId: 1`，并恢复主要中文文案。
- Git 已停止跟踪 `node_modules`、`backend/target`、旧 `.venv`、前端 `.js` 镜像和 `.vscode` 等本地生成文件。
- 后端已补充 JWT、密码兼容和用户上下文的最小回归测试。
- 前端已增加无依赖契约检查脚本，覆盖 401 处理、登录态恢复、mapper 编码保护和 mock/fallback 明示约束。
- Dashboard、Ability、Profile、Planning 的静默 mock/fallback 已收口为显式错误或演示数据提示。
- 后端关键本地 fallback 文案已统一为中文，AI 响应解析已支持从带解释文字的响应中提取 JSON。
- HTTP 400/500 错误响应体 code 已与 HTTP 状态对齐。

待完成：

- MySQL 与后端服务在线后完成端到端演示验收。
- 进一步移除历史 `decodeMaybeMojibake` 兼容层，改为从数据源彻底清理历史乱码。

## 1. 当前鉴权现状

### 后端

当前后端已有认证入口：

- `POST /api/auth/login`
- `POST /api/auth/register`
- `GET /api/auth/me`

当前用户识别集中在：

```text
backend/src/main/java/com/careerpilot/service/AuthSessionService.java
```

当前 token 格式：

```text
cp_user_{userId}_{random}
```

主要问题：

- token 不是 JWT，没有签名，理论上可以手工伪造用户 ID。
- token 没有过期时间。
- token 没有服务端黑名单、版本号或吊销机制。
- 没有统一认证过滤器/拦截器，每个 controller 手动调用 `currentUserId(request)`。
- 未登录、token 无效等认证错误目前抛 `IllegalArgumentException`，全局处理后返回 400，不是更标准的 401。

### 密码

当前密码存储字段：

```text
user_account.password_hash
user_account.salt
```

当前算法：

```text
SHA-256(salt + ":" + password)
```

主要问题：

- SHA-256 不适合作为密码存储算法，缺少 BCrypt/Argon2 这类慢哈希的抗暴力破解能力。
- 需要兼容已有用户，避免一改造就导致旧账号全部无法登录。

### 前端

当前前端已有：

- `localStorage` 保存 token。
- Axios 请求拦截器自动加 `Authorization: Bearer token`。
- Vue Router 根据本地 token 做路由守卫。

主要问题：

- 只要本地有 token 就认为已登录，没有启动时校验 `/auth/me`。
- 没有响应拦截器统一处理 401。
- token 失效时不会自动清理登录态并跳回登录页。
- 当前路由 meta 标题存在历史乱码，应后续修正。

## 2. 数据隔离现状

数据库和实体已经具备良好基础，大部分核心表都有 `user_id`：

- `user_profile`
- `chat_message`
- `career_plan`
- `target_offer`
- `ability_analysis`
- `learning_plan`
- `learning_task`
- `resume_material`
- `resume_document`
- `jd_analysis`
- `interview_record`

多数 controller 已经从请求中读取当前用户：

- 用户画像
- 职业规划
- 能力分析
- 简历素材
- JD 分析
- 简历定制
- 模拟面试
- 学习计划
- 仪表盘
- 目标 Offer

主要问题：

- 部分 service 仍保留 `DEFAULT_USER_ID = 1L` 或 `userId == null ? 1L : userId`。
- 如果将来某个 controller 漏传用户 ID，数据可能落到 1 号用户。
- AI Chat 当前 DTO 仍含 `userId`，需要改成只信任 token 中的当前用户，不能信任前端传入用户 ID。

## 3. AI 稳定性现状

当前已有 OpenAI-compatible Provider 和 fallback：

- AI 成功时，来源字段返回 `llm`。
- AI 未启用、调用失败、格式不符合预期时，来源字段返回 `local_fallback`。

主要问题：

- Prompt 和响应 JSON schema 需要进一步模板化。
- 各业务能力的 AI 解析与 fallback 标记还可以更统一。
- 部分 fallback 文案存在历史乱码，会影响用户体验和演示稳定性。
- 需要明确记录模型调用失败原因，但不能把密钥、完整请求体写入日志。

## 4. 前端 mock/fallback 现状

当前前端保留 mock 的主要位置：

```text
career-pilot-frontend/src/mock/careerPilotMock.ts
career-pilot-frontend/src/services/mockCareerService.ts
```

部分页面仍会在接口失败后使用 mock：

- Dashboard
- Ability
- Profile
- Planning
- Resume
- Interview

主要问题：

- 页面层 fallback 会掩盖真实接口错误，不利于定位后端问题。
- 有些 mock 是演示兜底，有些是历史占位，需要区分。
- 后续应优先真实接口，fallback 要显式提示“当前使用演示数据/本地规则”。

## 5. 后续实施顺序

### 第 2 步：后端 JWT 鉴权，完成后 25%

实施内容：

- 新增 JWT 配置项：secret、issuer、expiration。
- 改造 `AuthSessionService` 为签发、解析、校验 JWT。
- 增加认证异常类型，并让未登录/无效 token 返回 401。
- 保持 `/api/auth/login`、`/api/auth/register`、`/api/health` 可匿名访问。
- 核心接口继续通过 `currentUserId(request)` 获取当前用户，先不大规模重构 controller。

验收标准：

- 登录返回真正 JWT。
- 修改 token 中的用户 ID 后无法通过校验。
- 未携带 token 访问 `/api/profile/me` 返回 401。
- 携带有效 token 可以正常访问原有功能。

### 第 3 步：密码安全，完成后 35%

实施内容：

- 引入 Spring Security Crypto 或 BCrypt 实现。
- 新注册用户使用 BCrypt。
- 登录时兼容旧 SHA-256 密码，旧密码登录成功后自动迁移为 BCrypt。

验收标准：

- 数据库不再保存新用户的 SHA-256 密码。
- 旧测试账号仍可登录。
- 错误密码无法登录。

### 第 4 步：前端登录态，完成后 45%

实施内容：

- Axios 响应拦截器处理 401。
- 401 时清理 token，跳转 `/login`。
- 应用启动或进入受保护路由时调用 `/auth/me` 校验 token。
- 修复路由标题乱码。

验收标准：

- token 失效后页面自动回登录。
- 刷新页面后仍能识别当前用户。
- 无 token 不能进入业务页面。

### 第 5 步：数据隔离，完成后 60%

实施内容：

- 去除服务层默认 1 号用户兜底。
- AI Chat 改成后端从 token 获取用户 ID，不信任前端 userId。
- 检查所有查询、更新、删除是否带 `user_id` 条件。

验收标准：

- 用户 A 看不到用户 B 的画像、简历、面试、学习计划。
- 漏传 userId 不会静默写入 1 号用户。

### 第 6-7 步：AI 稳定性，完成后 85%

实施内容：

- 统一 Prompt 模板。
- 统一 JSON 解析、字段兜底和来源标记。
- 修复后端 fallback 中文乱码。
- 增加 AI 错误日志摘要。

验收标准：

- 大模型返回非标准 JSON 时功能不崩。
- 页面可明确看到 `llm` 或 `local_fallback` 来源。
- 关键 fallback 文案可读、可演示。

### 第 8 步：前端 mock 清理，完成后 93%

实施内容：

- 梳理每个页面的 mock 使用。
- 真实接口失败时优先提示错误。
- 仅保留明确的演示模式或本地 fallback。

验收标准：

- 页面不会无提示地吞掉真实接口错误。
- mock 数据位置集中、用途明确。

### 第 9 步：回归测试与文档，完成后 100%

实施内容：

- 跑通登录 -> 画像 -> 规划 -> 能力 -> 简历 -> JD -> 面试 -> 学习计划。
- 更新 README 和启动文档。
- 记录增强阶段验收结果。

验收标准：

- 前后端构建通过。
- MySQL、Backend、Frontend 全部 UP。
- 新鉴权和真实数据流可稳定演示。
