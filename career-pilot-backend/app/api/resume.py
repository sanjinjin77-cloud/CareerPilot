from fastapi import APIRouter, HTTPException, status

from app.core.deps import CurrentUserDep, DbDep
from app.schemas.career import (
    ResumeMaterialCreate,
    ResumeMaterialRead,
    ResumeMaterialUpdate,
)
from app.services.career_service import (
    create_resume_material,
    delete_resume_material,
    list_resume_materials,
    update_resume_material,
)

router = APIRouter()


@router.get("/status")
def resume_status() -> dict[str, str]:
    return {"module": "resume", "status": "ready"}


@router.get("/materials", response_model=list[ResumeMaterialRead])
def read_materials(
    current_user: CurrentUserDep, db: DbDep
) -> list[ResumeMaterialRead]:
    return [
        ResumeMaterialRead.model_validate(item)
        for item in list_resume_materials(db, current_user.id)
    ]


@router.post("/materials", response_model=ResumeMaterialRead)
def create_material(
    payload: ResumeMaterialCreate, current_user: CurrentUserDep, db: DbDep
) -> ResumeMaterialRead:
    material = create_resume_material(db, current_user.id, payload)
    return ResumeMaterialRead.model_validate(material)


@router.put("/materials/{material_id}", response_model=ResumeMaterialRead)
def update_material(
    material_id: int,
    payload: ResumeMaterialUpdate,
    current_user: CurrentUserDep,
    db: DbDep,
) -> ResumeMaterialRead:
    material = update_resume_material(db, current_user.id, material_id, payload)
    if material is None:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Material not found")
    return ResumeMaterialRead.model_validate(material)


@router.delete("/materials/{material_id}")
def delete_material(material_id: int, current_user: CurrentUserDep, db: DbDep) -> dict[str, bool]:
    deleted = delete_resume_material(db, current_user.id, material_id)
    if not deleted:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Material not found")
    return {"success": True}
