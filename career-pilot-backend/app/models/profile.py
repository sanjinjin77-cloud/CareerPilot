from datetime import datetime

from sqlalchemy import DateTime, ForeignKey, JSON, String, Text, UniqueConstraint, func
from sqlalchemy.orm import Mapped, mapped_column, relationship

from app.db.base import Base


class UserProfile(Base):
    __tablename__ = "user_profiles"
    __table_args__ = (UniqueConstraint("user_id", name="uq_user_profiles_user_id"),)

    id: Mapped[int] = mapped_column(primary_key=True, index=True)
    user_id: Mapped[int] = mapped_column(ForeignKey("users.id"), index=True)
    grade: Mapped[str] = mapped_column(String(32), default="")
    education_level: Mapped[str] = mapped_column(String(64), default="")
    school_type: Mapped[str] = mapped_column(String(64), default="")
    major: Mapped[str] = mapped_column(String(128), default="")
    current_stage: Mapped[str] = mapped_column(String(128), default="")
    target_position: Mapped[str] = mapped_column(String(128), default="")
    target_city: Mapped[str] = mapped_column(String(128), default="")
    target_industry: Mapped[str] = mapped_column(String(128), default="")
    languages: Mapped[str] = mapped_column(Text, default="")
    frameworks: Mapped[str] = mapped_column(Text, default="")
    projects: Mapped[str] = mapped_column(Text, default="")
    competitions: Mapped[str] = mapped_column(Text, default="")
    internships: Mapped[str] = mapped_column(Text, default="")
    english_level: Mapped[str] = mapped_column(String(128), default="")
    certificates: Mapped[str] = mapped_column(Text, default="")
    preferences: Mapped[list[str]] = mapped_column(JSON, default=list)
    created_at: Mapped[datetime] = mapped_column(DateTime, server_default=func.now())
    updated_at: Mapped[datetime] = mapped_column(
        DateTime, server_default=func.now(), onupdate=func.now()
    )

    user: Mapped["User"] = relationship(back_populates="profile")  # noqa: F821
