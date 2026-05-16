from fastapi import APIRouter, HTTPException, status

from app.core.deps import CurrentUserDep, DbDep
from app.schemas.profile import UserProfileCreate, UserProfileRead, UserProfileUpdate
from app.services.user_service import get_profile_by_user_id, upsert_profile

router = APIRouter()


@router.get("/status")
def profile_status() -> dict[str, str]:
    return {"module": "profile", "status": "ready"}


@router.get("/me", response_model=UserProfileRead)
def read_profile(current_user: CurrentUserDep, db: DbDep) -> UserProfileRead:
    profile = get_profile_by_user_id(db, current_user.id)
    if profile is None:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Profile has not been created",
        )
    return UserProfileRead.model_validate(profile)


@router.post("/me", response_model=UserProfileRead)
def create_profile(
    payload: UserProfileCreate, current_user: CurrentUserDep, db: DbDep
) -> UserProfileRead:
    profile = upsert_profile(db, current_user.id, payload)
    return UserProfileRead.model_validate(profile)


@router.put("/me", response_model=UserProfileRead)
def update_profile(
    payload: UserProfileUpdate, current_user: CurrentUserDep, db: DbDep
) -> UserProfileRead:
    profile = upsert_profile(db, current_user.id, payload)
    return UserProfileRead.model_validate(profile)
