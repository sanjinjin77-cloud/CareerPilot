from fastapi import APIRouter

from app.core.deps import CurrentUserDep
from app.schemas.auth import UserRead

router = APIRouter()


@router.get("/status")
def users_status() -> dict[str, str]:
    return {"module": "users", "status": "ready"}


@router.get("/me", response_model=UserRead)
def read_current_user(current_user: CurrentUserDep) -> UserRead:
    return UserRead.model_validate(current_user)
