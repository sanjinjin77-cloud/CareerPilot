from sqlalchemy import or_, select
from sqlalchemy.orm import Session

from app.core.security import hash_password, verify_password
from app.models.profile import UserProfile
from app.models.user import User
from app.schemas.auth import UserCreate
from app.schemas.profile import UserProfileCreate, UserProfileUpdate


def get_user_by_id(db: Session, user_id: int) -> User | None:
    return db.get(User, user_id)


def get_user_by_account(db: Session, account: str) -> User | None:
    statement = select(User).where(or_(User.username == account, User.email == account))
    return db.scalar(statement)


def create_user(db: Session, payload: UserCreate) -> User:
    user = User(
        username=payload.username,
        email=str(payload.email),
        password_hash=hash_password(payload.password),
    )
    db.add(user)
    db.commit()
    db.refresh(user)
    return user


def authenticate_user(db: Session, account: str, password: str) -> User | None:
    user = get_user_by_account(db, account)
    if not user or not verify_password(password, user.password_hash):
        return None
    return user


def get_profile_by_user_id(db: Session, user_id: int) -> UserProfile | None:
    statement = select(UserProfile).where(UserProfile.user_id == user_id)
    return db.scalar(statement)


def upsert_profile(
    db: Session, user_id: int, payload: UserProfileCreate | UserProfileUpdate
) -> UserProfile:
    profile = get_profile_by_user_id(db, user_id)
    data = payload.model_dump()

    if profile is None:
        profile = UserProfile(user_id=user_id, **data)
        db.add(profile)
    else:
        for field, value in data.items():
            setattr(profile, field, value)

    db.commit()
    db.refresh(profile)
    return profile
