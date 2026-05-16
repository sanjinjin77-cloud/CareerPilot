from datetime import datetime

from sqlalchemy import DateTime, ForeignKey, JSON, String, Text, func
from sqlalchemy.orm import Mapped, mapped_column

from app.db.base import Base


class CareerPlan(Base):
    __tablename__ = "career_plans"

    id: Mapped[int] = mapped_column(primary_key=True, index=True)
    user_id: Mapped[int] = mapped_column(ForeignKey("users.id"), index=True)
    stage: Mapped[str] = mapped_column(String(128), default="")
    target_position: Mapped[str] = mapped_column(String(128), default="")
    plan_content: Mapped[str] = mapped_column(Text, default="")
    short_term_goals: Mapped[list[str]] = mapped_column(JSON, default=list)
    mid_term_goals: Mapped[list[str]] = mapped_column(JSON, default=list)
    long_term_goals: Mapped[list[str]] = mapped_column(JSON, default=list)
    weekly_tasks: Mapped[list[str]] = mapped_column(JSON, default=list)
    created_at: Mapped[datetime] = mapped_column(DateTime, server_default=func.now())
    updated_at: Mapped[datetime] = mapped_column(
        DateTime, server_default=func.now(), onupdate=func.now()
    )


class AbilityReport(Base):
    __tablename__ = "ability_reports"

    id: Mapped[int] = mapped_column(primary_key=True, index=True)
    user_id: Mapped[int] = mapped_column(ForeignKey("users.id"), index=True)
    target_position: Mapped[str] = mapped_column(String(128), default="")
    match_score: Mapped[int] = mapped_column(default=0)
    strengths: Mapped[list[str]] = mapped_column(JSON, default=list)
    weaknesses: Mapped[list[str]] = mapped_column(JSON, default=list)
    suggestions: Mapped[list[str]] = mapped_column(JSON, default=list)
    created_at: Mapped[datetime] = mapped_column(DateTime, server_default=func.now())


class ResumeMaterial(Base):
    __tablename__ = "resume_materials"

    id: Mapped[int] = mapped_column(primary_key=True, index=True)
    user_id: Mapped[int] = mapped_column(ForeignKey("users.id"), index=True)
    type: Mapped[str] = mapped_column(String(64), default="")
    title: Mapped[str] = mapped_column(String(255), default="")
    original_content: Mapped[str] = mapped_column(Text, default="")
    optimized_content: Mapped[str] = mapped_column(Text, default="")
    tags: Mapped[list[str]] = mapped_column(JSON, default=list)
    created_at: Mapped[datetime] = mapped_column(DateTime, server_default=func.now())
    updated_at: Mapped[datetime] = mapped_column(
        DateTime, server_default=func.now(), onupdate=func.now()
    )


class JDAnalysis(Base):
    __tablename__ = "jd_analysis"

    id: Mapped[int] = mapped_column(primary_key=True, index=True)
    user_id: Mapped[int] = mapped_column(ForeignKey("users.id"), index=True)
    job_title: Mapped[str] = mapped_column(String(128), default="")
    company_name: Mapped[str] = mapped_column(String(128), default="")
    jd_content: Mapped[str] = mapped_column(Text, default="")
    keywords: Mapped[list[str]] = mapped_column(JSON, default=list)
    requirements: Mapped[list[str]] = mapped_column(JSON, default=list)
    match_score: Mapped[int] = mapped_column(default=0)
    recommended_materials: Mapped[list[str]] = mapped_column(JSON, default=list)
    suggestions: Mapped[list[str]] = mapped_column(JSON, default=list)
    created_at: Mapped[datetime] = mapped_column(DateTime, server_default=func.now())


class InterviewRecord(Base):
    __tablename__ = "interview_records"

    id: Mapped[int] = mapped_column(primary_key=True, index=True)
    user_id: Mapped[int] = mapped_column(ForeignKey("users.id"), index=True)
    job_title: Mapped[str] = mapped_column(String(128), default="")
    question: Mapped[str] = mapped_column(Text, default="")
    answer: Mapped[str] = mapped_column(Text, default="")
    feedback: Mapped[str] = mapped_column(Text, default="")
    scores: Mapped[dict] = mapped_column(JSON, default=dict)
    created_at: Mapped[datetime] = mapped_column(DateTime, server_default=func.now())
