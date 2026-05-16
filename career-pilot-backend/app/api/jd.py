from fastapi import APIRouter

from app.core.deps import CurrentUserDep, DbDep
from app.schemas.career import JDAnalysisCreate, JDAnalysisRead
from app.services.career_service import analyze_jd, list_jd_analysis

router = APIRouter()


@router.get("/status")
def jd_status() -> dict[str, str]:
    return {"module": "jd", "status": "ready"}


@router.get("/analysis", response_model=list[JDAnalysisRead])
def read_jd_analysis(current_user: CurrentUserDep, db: DbDep) -> list[JDAnalysisRead]:
    return [JDAnalysisRead.model_validate(item) for item in list_jd_analysis(db, current_user.id)]


@router.post("/analyze", response_model=JDAnalysisRead)
def create_jd_analysis(
    payload: JDAnalysisCreate, current_user: CurrentUserDep, db: DbDep
) -> JDAnalysisRead:
    analysis = analyze_jd(db, current_user.id, payload)
    return JDAnalysisRead.model_validate(analysis)
