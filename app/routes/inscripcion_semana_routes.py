from fastapi import APIRouter, Depends
from sqlalchemy.orm import Session

from app.database.connection import get_db
from app.firebase.firebase_dependencies import get_current_admin_user
from app.schemas.inscripcion_semana_schema import (
    InscripcionSemanaCreateSchema,
    InscripcionSemanaListResponseSchema,
    InscripcionSemanaMessageResponseSchema
)
from app.services.inscripcion_semana_service import (
    get_all_relaciones,
    get_semanas_by_inscripcion,
    get_inscripciones_by_semana,
    create_relacion,
    delete_relacion
)

router = APIRouter(prefix="/inscripciones-semanas", tags=["Inscripciones-Semanas"])
@router.get("/", response_model=InscripcionSemanaListResponseSchema)
def get_relaciones(
    admin=Depends(get_current_admin_user),
    db: Session = Depends(get_db)
):
    return get_all_relaciones(db)


@router.get("/inscripcion/{id_inscripcion}", response_model=InscripcionSemanaListResponseSchema)
def get_relaciones_por_inscripcion(
    id_inscripcion: int,
    admin=Depends(get_current_admin_user),
    db: Session = Depends(get_db)
):
    return get_semanas_by_inscripcion(db, id_inscripcion)


@router.get("/semana/{id_semana}", response_model=InscripcionSemanaListResponseSchema)
def get_relaciones_por_semana(
    id_semana: int,
    admin=Depends(get_current_admin_user),
    db: Session = Depends(get_db)
):
    return get_inscripciones_by_semana(db, id_semana)


@router.post("/", response_model=InscripcionSemanaMessageResponseSchema)
def create_new_relacion(
    relation_data: InscripcionSemanaCreateSchema,
    admin=Depends(get_current_admin_user),
    db: Session = Depends(get_db)
):
    return create_relacion(db, relation_data)


@router.delete(
    "/inscripcion/{id_inscripcion}/semana/{id_semana}",
    response_model=InscripcionSemanaMessageResponseSchema
)
def delete_existing_relacion(
    id_inscripcion: int,
    id_semana: int,
    admin=Depends(get_current_admin_user),
    db: Session = Depends(get_db)
):
    return delete_relacion(db, id_inscripcion, id_semana)
