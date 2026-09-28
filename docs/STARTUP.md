# CareerPilot 启动与排错手册

这份文档用于本地开发、演示前检查和常见启动问题排查。

## 1. 前置环境

请确认本机已安装：

- JDK 17+
- Maven
- Node.js 18+
- MySQL

快速检查：

```powershell
java -version
mvn -version
node -v
npm -v
```

## 2. 启动 MySQL

统一脚本不会自动启动 MySQL。启动项目前，请先确保 MySQL 监听 `3306`。

检查端口：

```powershell
Get-NetTCPConnection -LocalPort 3306 -State Listen
```

如果没有输出，说明 MySQL 未启动。常见启动方式：

```powershell
Get-Service *mysql*
Start-Service MySQL80
```

如果你的服务名不是 `MySQL80`，以 `Get-Service *mysql*` 显示的名称为准。

## 3. 配置本地环境变量

后端本地配置文件：

```text
backend/.env.local
```

可以从模板复制：

```powershell
Copy-Item backend\.env.example backend\.env.local
```

推荐内容：

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
```

启用大模型时：

```env
AI_ENABLED=true
AI_API_KEY=你的密钥
AI_MODEL=你的模型名
```

注意：不要把真实密钥写进 `application.yml`，也不要提交 `.env.local`。

## 4. 一键启动项目

在项目根目录执行：

```powershell
npm run dev
```

成功后会看到类似输出：

```text
CareerPilot is ready:
  Web:     http://127.0.0.1:5174
  Backend: http://127.0.0.1:8081/api/health
  Logs:    .dev/logs/
```

打开浏览器：

```text
http://127.0.0.1:5174
```

也可以直接运行：

```powershell
npm run dev:open
```

## 5. 状态检查与停止

查看状态：

```powershell
npm run dev:status
```

停止服务：

```powershell
npm run dev:stop
```

说明：

- `dev:stop` 只停止由统一脚本管理的前后端进程。
- MySQL 是系统服务，不会被 `dev:stop` 停掉。
- 如果某个端口是手动启动或外部程序占用，状态里会显示 `external`。

## 6. 单独启动后端

仅调试 Spring Boot 时：

```powershell
cd backend
mvn spring-boot:run
```

注意：这种方式不会自动读取 `backend/.env.local`。如果你依赖 AI Key 或自定义数据库密码，请先在当前终端设置环境变量，或优先使用根目录 `npm run dev`。

PowerShell 临时环境变量示例：

```powershell
$env:MYSQL_USERNAME='root'
$env:MYSQL_PASSWORD='123456'
$env:AI_ENABLED='true'
$env:AI_API_KEY='你的密钥'
$env:AI_MODEL='你的模型名'
mvn spring-boot:run
```

## 7. 单独启动前端

仅调试 Vue 页面时：

```powershell
cd career-pilot-frontend
npm install
npm run dev
```

此时仍需要后端在 `http://127.0.0.1:8081` 可用，否则页面请求会失败或进入 fallback 展示。

## 8. 常见问题

### 网站打不开

先看服务状态：

```powershell
npm run dev:status
```

如果 Frontend 是 DOWN，重新运行：

```powershell
npm run dev
```

如果状态是 UP，但浏览器打不开，确认访问的是：

```text
http://127.0.0.1:5174
```

### 登录失败

检查后端健康状态：

```powershell
Invoke-WebRequest http://127.0.0.1:8081/api/health -UseBasicParsing
```

如果后端不可用，查看日志：

```powershell
Get-Content .dev\logs\backend.err.log -Tail 80
Get-Content .dev\logs\backend.out.log -Tail 80
```

常见原因：

- MySQL 未启动。
- MySQL 密码不是 `123456`，但 `.env.local` 没改。
- `8081` 被其他程序占用。
- schema 初始化失败。

### Maven 可以用，但后端启动失败

Maven 只是构建和启动工具。`mvn -version` 成功只能说明 Maven 可用，不代表数据库、端口、环境变量都正确。

推荐用：

```powershell
npm run dev
```

因为统一脚本会先检查 MySQL、端口、健康检查和日志。

### 端口被占用

检查占用进程：

```powershell
Get-NetTCPConnection -LocalPort 8081 -State Listen
Get-NetTCPConnection -LocalPort 5174 -State Listen
```

如果是统一脚本启动的服务：

```powershell
npm run dev:stop
```

如果是外部进程，请先确认它不是你需要保留的程序，再手动关闭。

### AI 没有生效

检查 `backend/.env.local`：

```env
AI_ENABLED=true
AI_API_KEY=非空
AI_MODEL=非空
```

然后重启后端：

```powershell
npm run dev:stop
npm run dev
```

如果模型调用失败，系统会返回 `local_fallback`，说明功能链路仍然可用，但结果来自本地规则。

## 9. 构建验证

前端：

```powershell
cd career-pilot-frontend
npm run build
```

后端：

```powershell
cd backend
mvn -q -DskipTests compile
```

演示前建议执行：

```powershell
npm run dev:status
```
