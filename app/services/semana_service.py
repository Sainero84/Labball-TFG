from fastapi import HTTPException
from sqlalchemy.orm import Session

from app.repositories import semana_repository
from app.schemas.semana_schema import (
    SemanaCreateSchema,
    SemanaUpdateSchema,
    SemanaResponseSchema,
    SemanaListResponseSchema,
    SemanaMessageResponseSchema
)


# --------------------------------------------------
# FUNCIÓN AUXILIAR
# --------------------------------------------------

def to_semana_response(semana) -> SemanaResponseSchema:
    return SemanaResponseSchema(
        id_semana=semana.id_semana,
        semana=semana.semana,
        horario=semana.horario
    )


# --------------------------------------------------
# OBTENER TODAS LAS SEMANAS
# --------------------------------------------------

def get_all_semanas(db: Session) -> SemanaListResponseSchema:
    semanas = semana_repository.get_all(db)

    return SemanaListResponseSchema(
        semanas=[to_semana_response(semana) for semana in semanas]
    )


# --------------------------------------------------
# OBTENER UNA SEMANA POR ID
# --------------------------------------------------

def get_semana_by_id(db: Session, semana_id: int) -> SemanaResponseSchema:
    semana = semana_repository.get_by_id(db, semana_id)

    if semana is None:
        raise HTTPException(status_code=404, detail="Semana no encontrada")

    return to_semana_response(semana)


# --------------------------------------------------
# CREAR UNA SEMANA
# --------------------------------------------------

def create_semana(db: Session, semana_data: SemanaCreateSchema) -> SemanaMessageResponseSchema:
    new_semana_data = {
        "semana": semana_data.semana,
        "horario": semana_data.horario
    }

    new_semana = semana_repository.create(db, new_semana_data)

    return SemanaMessageResponseSchema(
        message="Semana creada correctamente",
        semana=to_semana_response(new_semana)
    )


# --------------------------------------------------
# ACTUALIZAR UNA SEMANA
# --------------------------------------------------

def update_semana(
    db: Session,
    semana_id: int,
    semana_data: SemanaUpdateSchema
) -> SemanaMessageResponseSchema:
    updated_fields = {}

    if semana_data.semana is not None:
        updated_fields["semana"] = semana_data.semana

    if semana_data.horario is not None:
        updated_fields["horario"] = semana_data.horario

    updated_semana = semana_repository.update(db, semana_id, updated_fields)

    if updated_semana is None:
        raise HTTPException(status_code=404, detail="Semana no encontrada")

    return SemanaMessageResponseSchema(
        message="Semana actualizada correctamente",
        semana=to_semana_response(updated_semana)
    )


# --------------------------------------------------
# BORRAR UNA SEMANA
# --------------------------------------------------

def delete_semana(db: Session, semana_id: int) -> SemanaMessageResponseSchema:
    deleted_semana = semana_repository.delete(db, semana_id)

    if deleted_semana is None:
        raise HTTPException(status_code=404, detail="Semana no encontrada")

    return SemanaMessageResponseSchema(
        message="Semana eliminada correctamente",
        semana=to_semana_response(deleted_semana)
    )