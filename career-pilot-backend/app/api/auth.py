from fastapi import APIRouter, HTTPException, status
from sqlalchemy.exc import IntegrityError

from app.core.deps import CurrentUserDep, DbDep
from app.core.security import create_access_token
from app.schemas.auth import TokenResponse, UserCreate, UserLogin, UserRead
from app.services.user_service import authenticate_user, create_user

router = APIRouter()


@router.get("/status")
def auth_status() -> dict[str, str]:
    return {"module": "auth", "status": "ready"}


@router.post("/register", response_model=TokenResponse)
def register(payload: UserCreate, db: DbDep) -> TokenResponse:
    try:
        user = create_user(db, payload)
    except IntegrityError as exc:
        db.rollback()
        raise HTTPException(
            status_code=status.HTTP_409_CONFLICT,
            detail="Username or email already exists",
        ) from exc

    token = create_access_token(str(user.id))
    return TokenResponse(access_token=token, user=UserRead.model_validate(user))


@router.post("/login", response_model=TokenResponse)
def login(payload: UserLogin, db: DbDep) -> TokenResponse:
    user = authenticate_user(db, payload.account, payload.password)
    if user is None:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Incorrect account or password",
        )

    token = create_access_token(str(user.id))
    return TokenResponse(access_token=token, user=UserRead.model_validate(user))


@router.get("/me", response_model=UserRead)
def read_me(current_user: CurrentUserDep) -> UserRead:
    return UserRead.model_validate(current_user)
