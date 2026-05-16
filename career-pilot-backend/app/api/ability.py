from fastapi import APIRouter

from app.core.deps import CurrentUserDep, DbDep
from app.schemas.career import AbilityReportCreate, AbilityReportRead
from app.services.career_service import create_ability_report, list_ability_reports

router = APIRouter()


@router.get("/status")
def ability_status() -> dict[str, str]:
    return {"module": "ability", "status": "ready"}


@router.get("", response_model=list[AbilityReportRead])
def read_ability_reports(
    current_user: CurrentUserDep, db: DbDep
) -> list[AbilityReportRead]:
    return [
        AbilityReportRead.model_validate(item)
        for item in list_ability_reports(db, current_user.id)
    ]


@router.post("/analyze", response_model=AbilityReportRead)
def analyze_ability(
    payload: AbilityReportCreate, current_user: CurrentUserDep, db: DbDep
) -> AbilityReportRead:
    report = create_ability_report(db, current_user.id, payload)
    return AbilityReportRead.model_validate(report)
