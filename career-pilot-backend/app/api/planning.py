from fastapi import APIRouter

from app.core.deps import CurrentUserDep, DbDep
from app.schemas.career import CareerPlanCreate, CareerPlanRead
from app.services.career_service import create_career_plan, list_career_plans

router = APIRouter()


@router.get("/status")
def planning_status() -> dict[str, str]:
    return {"module": "planning", "status": "ready"}


@router.get("", response_model=list[CareerPlanRead])
def read_plans(current_user: CurrentUserDep, db: DbDep) -> list[CareerPlanRead]:
    return [CareerPlanRead.model_validate(item) for item in list_career_plans(db, current_user.id)]


@router.post("/generate", response_model=CareerPlanRead)
def generate_plan(
    payload: CareerPlanCreate, current_user: CurrentUserDep, db: DbDep
) -> CareerPlanRead:
    plan = create_career_plan(db, current_user.id, payload)
    return CareerPlanRead.model_validate(plan)
