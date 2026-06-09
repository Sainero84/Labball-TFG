from fastapi import APIRouter, Depends
from sqlalchemy.orm import Session

from app.database.connection import get_db
from app.firebase.firebase_dependencies import get_current_admin_user
from app.schemas.semana_schema import (
    SemanaCreateSchema,
    SemanaUpdateSchema,
    SemanaResponseSchema,
    SemanaListResponseSchema,
    SemanaMessageResponseSchema
)
from app.services.semana_service import (
    get_all_semanas,
    get_semana_by_id,
    create_semana,
    update_semana,
    delete_semana
)

router = APIRouter(prefix="/semanas", tags=["Semanas"])


@router.get("/", response_model=SemanaListResponseSchema)
def get_semanas(
    admin=Depends(get_current_admin_user),
    db: Session = Depends(get_db)
):
    return get_all_semanas(db)


@router.get("/{semana_id}", response_model=SemanaResponseSchema)
def get_semana(
    semana_id: int,
    admin=Depends(get_current_admin_user),
    db: Session = Depends(get_db)
):
    return get_semana_by_id(db, semana_id)


@router.post("/", response_model=SemanaMessageResponseSchema)
def create_new_semana(
    semana_data: SemanaCreateSchema,
    admin=Depends(get_current_admin_user),
    db: Session = Depends(get_db)
):
    return create_semana(db, semana_data)


@router.put("/{semana_id}", response_model=SemanaMessageResponseSchema)
def update_existing_semana(
    semana_id: int,
    semana_data: SemanaUpdateSchema,
    admin=Depends(get_current_admin_user),
    db: Session = Depends(get_db)
):
    return update_semana(db, semana_id, semana_data)


@router.delete("/{semana_id}", response_model=SemanaMessageResponseSchema)
def delete_existing_semana(
    semana_id: int,
    admin=Depends(get_current_admin_user),
    db: Session = Depends(get_db)
):
    return delete_semana(db, semana_id)
