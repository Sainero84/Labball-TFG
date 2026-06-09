from fastapi import APIRouter, Depends
from sqlalchemy.orm import Session

from app.database.connection import get_db
from app.firebase.firebase_dependencies import get_current_admin_user
from app.schemas.inscripcion_schema import (
    InscripcionCreateSchema,
    InscripcionUpdateSchema,
    InscripcionResponseSchema,
    InscripcionListResponseSchema,
    InscripcionMessageResponseSchema
)
from app.services.inscripcion_service import (
    get_all_inscripciones,
    get_inscripcion_by_id,
    create_inscripcion,
    update_inscripcion,
    delete_inscripcion
)

router = APIRouter(prefix="/inscripciones", tags=["Inscripciones"])


@router.get("/", response_model=InscripcionListResponseSchema)
def get_inscripciones(
    admin=Depends(get_current_admin_user),
    db: Session = Depends(get_db)
):
    return get_all_inscripciones(db)


@router.get("/{inscripcion_id}", response_model=InscripcionResponseSchema)
def get_inscripcion(
    inscripcion_id: int,
    admin=Depends(get_current_admin_user),
    db: Session = Depends(get_db)
):
    return get_inscripcion_by_id(db, inscripcion_id)


@router.post("/", response_model=InscripcionMessageResponseSchema)
def create_new_inscripcion(
    inscripcion_data: InscripcionCreateSchema,
    admin=Depends(get_current_admin_user),
    db: Session = Depends(get_db)
):
    return create_inscripcion(db, inscripcion_data)


@router.put("/{inscripcion_id}", response_model=InscripcionMessageResponseSchema)
def update_existing_inscripcion(
    inscripcion_id: int,
    inscripcion_data: InscripcionUpdateSchema,
    admin=Depends(get_current_admin_user),
    db: Session = Depends(get_db)
):
    return update_inscripcion(db, inscripcion_id, inscripcion_data)


@router.delete("/{inscripcion_id}", response_model=InscripcionMessageResponseSchema)
def delete_existing_inscripcion(
    inscripcion_id: int,
    admin=Depends(get_current_admin_user),
    db: Session = Depends(get_db)
):
    return delete_inscripcion(db, inscripcion_id)
