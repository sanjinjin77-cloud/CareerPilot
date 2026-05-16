from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware

from app.api import ability, auth, interview, jd, planning, profile, resume, users
from app.core.config import settings
from app.db.session import init_db

app = FastAPI(
    title=settings.PROJECT_NAME,
    version=settings.VERSION,
    description="CareerPilot backend API",
)

app.add_middleware(
    CORSMiddleware,
    allow_origins=settings.CORS_ORIGINS,
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

app.include_router(auth.router, prefix="/api/auth", tags=["auth"])
app.include_router(users.router, prefix="/api/users", tags=["users"])
app.include_router(profile.router, prefix="/api/profile", tags=["profile"])
app.include_router(planning.router, prefix="/api/planning", tags=["planning"])
app.include_router(ability.router, prefix="/api/ability", tags=["ability"])
app.include_router(resume.router, prefix="/api/resume", tags=["resume"])
app.include_router(jd.router, prefix="/api/jd", tags=["jd"])
app.include_router(interview.router, prefix="/api/interview", tags=["interview"])


@app.on_event("startup")
def on_startup() -> None:
    init_db()


@app.get("/api/health")
def health_check() -> dict[str, str]:
    return {"status": "ok", "service": settings.PROJECT_NAME}
