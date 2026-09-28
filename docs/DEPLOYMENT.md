# CareerPilot Deployment Guide

This guide covers the first production-readiness path for CareerPilot: containerized backend + MySQL, with the frontend deployed as static files.

## 1. Backend + MySQL With Docker Compose

Copy the production environment template and fill real secrets:

```powershell
Copy-Item .env.production.example .env
```

Then start the services:

```powershell
docker compose up -d --build
```

Check service health:

```powershell
docker compose ps
Invoke-WebRequest http://127.0.0.1:8081/api/health -UseBasicParsing
```

The backend container listens on `8081` by default. Change `BACKEND_PORT` in `.env` if the host port is occupied.

If the deployment host already has MySQL on `3306`, change `MYSQL_PORT` in `.env`. The backend container still connects to the internal Docker service at `mysql:3306`.

## 2. Database Migrations

CareerPilot uses Flyway to manage MySQL schema changes.

Migration files live in:

```text
backend/src/main/resources/db/migration
```

The first migration is:

```text
V1__initial_schema.sql
```

For a new database, Flyway creates the tables and seed data automatically on backend startup.

For an existing local database that was created before Flyway was introduced, `FLYWAY_BASELINE_ON_MIGRATE=true` lets Flyway create its history table and take ownership safely.

Production defaults:

```env
SQL_INIT_MODE=never
FLYWAY_ENABLED=true
FLYWAY_BASELINE_ON_MIGRATE=true
```

Future schema changes should be added as new migration files such as:

```text
V2__add_ai_usage_table.sql
V3__add_resume_export_fields.sql
```

## 3. Frontend Production Build

The frontend already supports `VITE_API_BASE_URL`. For a same-origin deployment behind Nginx or a platform rewrite, keep:

```env
VITE_API_BASE_URL=/api
```

Build:

```powershell
cd career-pilot-frontend
npm install
npm run build
```

Deploy `career-pilot-frontend/dist` to a static host or serve it with Nginx.

## 4. Reverse Proxy Shape

Recommended production routing:

```text
https://your-domain.example/      -> frontend static files
https://your-domain.example/api/* -> backend http://backend:8081/api/*
```

With this shape, the frontend can keep `VITE_API_BASE_URL=/api`.

## 5. Required Secret Rules

- Do not commit `.env`.
- Replace `JWT_SECRET` before public deployment.
- Replace `MYSQL_ROOT_PASSWORD` before public deployment.
- Keep `AI_API_KEY` only in server-side environment variables.
- Do not expose the backend directly unless CORS and rate limits are configured.

## 6. AI Quota And Usage Records

AI-enabled requests are limited per user by default:

```env
AI_QUOTA_ENABLED=true
AI_DAILY_LIMIT=20
```

The backend stores only feature, source, model, duration, success status and a short error summary in `ai_call_record`. Prompts, request bodies, API keys and model responses are not stored in the usage table.

When the limit is reached, the API returns HTTP `429`. Requests that use local rules because the provider is disabled do not consume the LLM quota, but are still recorded as `local_fallback` for observability.

## 7. Current Limitations

This first deployment layer does not yet include:

- Production CORS allowlist.
- Centralized logs and monitoring.

Those are the next production-readiness steps after the containerized baseline is verified.
