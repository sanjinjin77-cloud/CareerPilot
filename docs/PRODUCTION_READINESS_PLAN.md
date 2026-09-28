# CareerPilot 上线准备计划

本文档用于把 CareerPilot 从“本地演示/答辩可验收”推进到“可公开试用、可维护、可监控、可逐步商业化”的上线版本。

## 1. 当前判断

### 当前状态

- 演示/答辩完成度：100%
- 可上线产品完成度：约 87%
- 生产级稳定完成度：约 65%

### 已具备的上线基础

- 前端：Vue 3 + TypeScript + Vite + Element Plus，主要业务页面已经成链路。
- 后端：Spring Boot 3 + MyBatis Plus + MySQL，核心数据已进入数据库。
- 认证：已有注册、登录、JWT、401 清理、用户数据隔离基础。
- AI：已接入 OpenAI-compatible 接口，并保留 `llm` / `local_fallback` 来源标记。
- 本地启动：根目录 `npm run dev` 可统一拉起前后端，后端默认端口为 `8081`。
- 部署入口：已新增后端 Dockerfile、根目录 docker-compose、生产环境变量模板和部署说明。
- 回归保护：已有后端鉴权/密码/用户上下文测试，前端已有契约验证脚本、类型检查和构建验证。

### 上线前核心差距

当前项目的主要差距不是功能数量，而是生产环境稳定性：

- 配置、密钥、数据库、CORS、AI 参数需要完全环境变量化。
- 数据库初始化已引入 Flyway 基础迁移，后续新增字段应继续使用版本化 migration。
- API 返回结构需要进一步统一。
- AI 成本、失败、超时、JSON 校验和调用日志需要产品级治理。
- 前端新用户空状态、错误状态、fallback 展示还需要更精细。
- 缺少 Docker、部署说明、线上健康检查、日志与监控方案。

## 2. 推荐上线形态

第一版不建议直接做成高并发 SaaS。更稳的路线是：

1. 公开 Demo 站点
2. 可注册试用
3. 限制 AI 调用额度
4. 保留演示数据入口
5. 收集真实用户反馈后再扩展学校/就业办管理能力

这样可以最大化展示项目价值，同时控制成本、安全和运维压力。

## 3. 推荐部署架构

### 最小上线架构

```text
User Browser
  |
  | HTTPS
  v
Static Frontend CDN / Nginx
  |
  | /api
  v
Spring Boot Backend
  |
  | JDBC
  v
Cloud MySQL
  |
  | HTTPS
  v
OpenAI-compatible LLM Provider
```

### 推荐选型

- 前端：Vercel、Netlify、Cloudflare Pages，或 Nginx 静态托管。
- 后端：Docker 部署到云服务器、Railway、Render、Fly.io、阿里云轻量应用服务器。
- 数据库：云 MySQL。后续也可迁移 PostgreSQL，但第一版不建议同时换库。
- 域名：主站 `https://careerpilot.example.com`，后端可用 `/api` 反向代理，避免前端暴露独立后端域名。
- 证书：平台自动 HTTPS，或 Nginx + Let's Encrypt。

## 4. 环境变量清单

上线环境至少需要这些变量：

```env
SERVER_PORT=8081

MYSQL_HOST=
MYSQL_PORT=3306
MYSQL_DATABASE=career_pilot
MYSQL_USERNAME=
MYSQL_PASSWORD=

JWT_SECRET=
JWT_ISSUER=career-pilot
JWT_EXPIRATION_SECONDS=86400

AI_ENABLED=true
AI_BASE_URL=https://api.openai.com/v1
AI_API_KEY=
AI_MODEL=
AI_TIMEOUT_SECONDS=60
AI_QUOTA_ENABLED=true
AI_DAILY_LIMIT=20

CORS_ALLOWED_ORIGINS=https://careerpilot.example.com
LOG_LEVEL=INFO
```

上线前要求：

- `JWT_SECRET` 必须使用高强度随机字符串。
- `AI_API_KEY` 不允许进入前端构建产物、Git、日志。
- 数据库密码不写入 `application.yml`。
- CORS 只允许生产域名和必要的预览域名。

## 5. 上线差距清单

### P0：必须完成

- Dockerfile：后端可构建成独立镜像。
- Docker Compose：本地可一键拉起后端 + MySQL。
- 生产配置：数据库、JWT、AI、CORS 全部从环境变量读取。
- 数据库迁移：引入 Flyway 或 Liquibase，替代直接依赖 `schema.sql` 初始化。
- 健康检查：保留并文档化 `/api/health`。
- 前端生产 API 地址：使用环境变量或部署平台 rewrites/proxy，不写死本机地址。
- AI 安全日志：只记录功能名、耗时、来源、错误摘要，不记录密钥和完整请求体。
- AI 调用额度：已实现按用户每日限制，防止公开 Demo 被刷爆成本。
- 管理员种子策略：生产环境不自动创建弱密码账号。

### P1：强烈建议完成

- API 返回结构统一：逐步把裸 `Map` 收口到统一响应体或明确分层。
- 统一错误码：401、403、404、409、422、500 区分清楚。
- 前端空状态：新用户第一次进入各页面时给出明确下一步。
- 前端错误状态：后端不可用、AI 降级、无数据、权限过期分别展示。
- E2E 脚本：把当前手工链路固化为可重复脚本。
- 接口限流：登录、注册、AI 生成类接口需要限流。
- 日志文件轮转：避免服务器长期运行撑爆磁盘。

### P2：上线后迭代

- 管理后台：用户、AI 调用、错误日志、成本统计。
- 支付或邀请码：控制试用人群和 AI 成本。
- 邮箱验证、找回密码。
- 简历导出 PDF / DOCX。
- 多模型配置和模型调用效果对比。
- 用户反馈入口和问题上报。

## 6. 分阶段执行路线

### 阶段 1：生产配置收口

目标完成度：65% -> 72%（已完成）

当前状态：已完成基础版本。

工作内容：

- 后端配置支持通过 `MYSQL_URL`、`SERVER_PORT`、JWT 和 AI 环境变量覆盖。
- 前端 API base URL 已通过 `VITE_API_BASE_URL` 支持生产配置。
- 已补充根目录、后端、前端生产环境变量模板。
- 已补充后端 Dockerfile、根目录 `docker-compose.yml` 和部署说明。

验收标准：

- 本地不依赖写死密钥。
- 生产环境可以只靠环境变量启动。
- `npm run dev:status` 本地仍可用。

### 阶段 2：容器化与数据库迁移

目标完成度：72% -> 80%（已完成）

当前状态：基础版本已完成。

工作内容：

- 已新增后端 Dockerfile。
- 已新增部署用 docker-compose 示例。
- 已引入 Flyway。
- 已把现有 schema 转成首个 migration：`V1__initial_schema.sql`。

验收标准：

- 新机器可按文档启动后端和数据库。
- 数据库迁移可重复执行，不破坏已有数据。

### 阶段 3：AI 成本和可靠性治理

目标完成度：80% -> 87%（已完成）

当前状态：核心版本已完成。

工作内容：

- AI 调用记录表和每日用量表。
- 每用户每日调用额度，默认每日 20 次。
- AI 返回 JSON schema 校验。
- 超时、失败、fallback 统一策略。
- 前端显示 `AI 生成` / `本地规则生成`。

验收标准：

- AI 不可用时业务链路仍可跑通。
- 日志可定位失败功能和错误摘要。
- 公开试用不会无限消耗模型额度。

### 阶段 4：产品体验上线收口

目标完成度：87% -> 93%

工作内容：

- 新用户引导。
- 页面空状态和错误状态。
- 移动端主要页面适配。
- 演示数据入口明确标记。
- 统一 loading、toast、异常提示。

验收标准：

- 新用户 5 分钟内能完成画像、规划、简历素材、模拟面试的核心体验。
- 页面失败不会静默展示假数据。

### 阶段 5：线上验收与发布材料

目标完成度：93% -> 100%

工作内容：

- 生产构建。
- 云端部署。
- 域名和 HTTPS。
- 健康检查。
- 完整链路验收。
- 上线 README、部署说明、演示账号说明。

验收标准：

- 公网可访问。
- 注册/登录可用。
- 完整业务闭环可跑通。
- 后台日志可定位错误。
- AI 来源字段清晰可见。

## 7. 完整上线验收清单

上线前必须跑通：

```text
注册/登录
-> 用户画像
-> 职业规划
-> 能力分析
-> 学习任务
-> 简历素材
-> JD 分析
-> 定制简历
-> 模拟面试
-> 面试复盘任务
```

每一步需要记录：

- 接口是否 2xx。
- 页面是否无白屏。
- 是否使用 `llm` 或 `local_fallback`。
- 数据是否绑定当前用户。
- 刷新页面后状态是否可恢复。

## 8. 推荐近期任务顺序

1. 新增生产环境配置模板。
2. 新增后端 Dockerfile。
3. 新增 docker-compose 部署样例。
4. 引入 Flyway，迁移现有 schema。
5. 固化端到端验收脚本。
6. 增加 AI 调用记录和额度限制。
7. 前端生产 API 地址收口。
8. 写正式上线部署文档。

## 9. 当前结论

CareerPilot 已经不是“缺功能”的状态，而是进入上线前工程治理阶段。

最推荐的上线策略是：先发布一个受控公开 Demo，限制 AI 调用额度，保留完整职业成长闭环，优先验证真实用户是否愿意使用“画像 -> 规划 -> 简历 -> 面试复盘”这条链路。

完成本文档中的 P0 项后，项目可以进入小范围公开试用；完成 P1 项后，可以作为较稳定的作品集级线上产品；P2 项属于长期产品化路线。
