from fastapi import HTTPException
from sqlalchemy.orm import Session

from app.repositories import (
    inscripcion_semana_repository,
    inscripcion_repository,
    semana_repository
)
from app.schemas.inscripcion_semana_schema import (
    InscripcionSemanaCreateSchema,
    InscripcionSemanaResponseSchema,
    InscripcionSemanaMessageResponseSchema,
    InscripcionSemanaListResponseSchema
)
# --------------------------------------------------
# FUNCIÓN AUXILIAR
# --------------------------------------------------

def to_relation_response(relation) -> InscripcionSemanaResponseSchema:
    return InscripcionSemanaResponseSchema(
        id_inscripcion=relation.id_inscripcion,
        id_semana=relation.id_semana
    )

# --------------------------------------------------
# OBTENER TODAS LAS RELACIONES
# --------------------------------------------------

def get_all_relaciones(db: Session) -> InscripcionSemanaListResponseSchema:
    relaciones = inscripcion_semana_repository.get_all(db)

    return InscripcionSemanaListResponseSchema(
        relaciones=[to_relation_response(r) for r in relaciones]
    )


# --------------------------------------------------
# OBTENER TODAS LAS SEMANAS DE UNA INSCRIPCIÓN
# --------------------------------------------------

def get_semanas_by_inscripcion(
    db: Session,
    id_inscripcion: int
) -> InscripcionSemanaListResponseSchema:
    inscripcion = inscripcion_repository.get_by_id(db, id_inscripcion)

    if inscripcion is None:
        raise HTTPException(status_code=404, detail="Inscripción no encontrada")

    relaciones = inscripcion_semana_repository.get_by_inscripcion_id(db, id_inscripcion)

    return InscripcionSemanaListResponseSchema(
        relaciones=[to_relation_response(r) for r in relaciones]
    )


# --------------------------------------------------
# OBTENER TODAS LAS INSCRIPCIONES DE UNA SEMANA
# --------------------------------------------------

def get_inscripciones_by_semana(
    db: Session,
    id_semana: int
) -> InscripcionSemanaListResponseSchema:
    semana = semana_repository.get_by_id(db, id_semana)

    if semana is None:
        raise HTTPException(status_code=404, detail="Semana no encontrada")

    relaciones = inscripcion_semana_repository.get_by_semana_id(db, id_semana)

    return InscripcionSemanaListResponseSchema(
        relaciones=[to_relation_response(r) for r in relaciones]
    )

# --------------------------------------------------
# CREAR UNA RELACIÓN
# --------------------------------------------------

def create_relacion(
    db: Session,
    relation_data: InscripcionSemanaCreateSchema
) -> InscripcionSemanaMessageResponseSchema:
    # Validamos que la inscripción exista
    inscripcion = inscripcion_repository.get_by_id(db, relation_data.id_inscripcion)
    if inscripcion is None:
        raise HTTPException(status_code=404, detail="Inscripción no encontrada")

    # Validamos que la semana exista
    semana = semana_repository.get_by_id(db, relation_data.id_semana)
    if semana is None:
        raise HTTPException(status_code=404, detail="Semana no encontrada")

    # Validamos que la relación no exista ya
    existing_relation = inscripcion_semana_repository.get_by_ids(
        db,
        relation_data.id_inscripcion,
        relation_data.id_semana
    )
    if existing_relation is not None:
        raise HTTPException(status_code=400, detail="La relación ya existe")

    new_relation_data = {
        "id_inscripcion": relation_data.id_inscripcion,
        "id_semana": relation_data.id_semana
    }

    new_relation = inscripcion_semana_repository.create(db, new_relation_data)

    return InscripcionSemanaMessageResponseSchema(
        message="Relación creada correctamente",
        relacion=to_relation_response(new_relation)
    )

# --------------------------------------------------
# BORRAR UNA RELACIÓN
# --------------------------------------------------

def delete_relacion(
    db: Session,
    id_inscripcion: int,
    id_semana: int
) -> InscripcionSemanaMessageResponseSchema:
    deleted_relation = inscripcion_semana_repository.delete(db, id_inscripcion, id_semana)

    if deleted_relation is None:
        raise HTTPException(status_code=404, detail="Relación no encontrada")

    return InscripcionSemanaMessageResponseSchema(
        message="Relación eliminada correctamente",
        relacion=to_relation_response(deleted_relation)
    )