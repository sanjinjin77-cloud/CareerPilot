from fastapi import APIRouter

from app.core.deps import CurrentUserDep, DbDep
from app.schemas.career import (
    InterviewAnswerCreate,
    InterviewQuestionCreate,
    InterviewRecordRead,
)
from app.services.career_service import (
    generate_interview_question,
    list_interview_records,
    score_interview_answer,
)

router = APIRouter()


@router.get("/status")
def interview_status() -> dict[str, str]:
    return {"module": "interview", "status": "ready"}


@router.get("/records", response_model=list[InterviewRecordRead])
def read_interview_records(
    current_user: CurrentUserDep, db: DbDep
) -> list[InterviewRecordRead]:
    return [
        InterviewRecordRead.model_validate(item)
        for item in list_interview_records(db, current_user.id)
    ]


@router.post("/question")
def create_interview_question(
    payload: InterviewQuestionCreate, current_user: CurrentUserDep
) -> dict[str, str]:
    return generate_interview_question(payload)


@router.post("/answer", response_model=InterviewRecordRead)
def create_interview_answer(
    payload: InterviewAnswerCreate, current_user: CurrentUserDep, db: DbDep
) -> InterviewRecordRead:
    record = score_interview_answer(db, current_user.id, payload)
    return InterviewRecordRead.model_validate(record)
