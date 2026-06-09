from fastapi import HTTPException
from sqlalchemy.orm import Session

from app.repositories import descuento_repository
from app.schemas.descuento_schema import (
    DescuentoCreateSchema,
    DescuentoUpdateSchema,
    DescuentoResponseSchema,
    DescuentoListResponseSchema,
    DescuentoMessageResponseSchema
)
# --------------------------------------------------
# FUNCIÓN AUXILIAR
# --------------------------------------------------

def to_descuento_response(descuento) -> DescuentoResponseSchema:
    return DescuentoResponseSchema(
        id_descuento=descuento.id_descuento,
        codigo=descuento.codigo,
        porcentaje=float(descuento.porcentaje)
    )

# --------------------------------------------------
# OBTENER TODOS LOS DESCUENTOS
# --------------------------------------------------

def get_all_descuentos(db: Session) -> DescuentoListResponseSchema:
    descuentos = descuento_repository.get_all(db)

    return DescuentoListResponseSchema(
        descuentos=[to_descuento_response(descuento) for descuento in descuentos]
    )


# --------------------------------------------------
# OBTENER UN DESCUENTO POR ID
# --------------------------------------------------

def get_descuento_by_id(db: Session, descuento_id: int) -> DescuentoResponseSchema:
    descuento = descuento_repository.get_by_id(db, descuento_id)

    if descuento is None:
        raise HTTPException(status_code=404, detail="Descuento no encontrado")

    return to_descuento_response(descuento)

# --------------------------------------------------
# CREAR UN DESCUENTO
# --------------------------------------------------

def create_descuento(
    db: Session,
    descuento_data: DescuentoCreateSchema
) -> DescuentoMessageResponseSchema:
    existing = descuento_repository.get_by_codigo(db, descuento_data.codigo)

    if existing is not None:
        raise HTTPException(status_code=400, detail="Ya existe un descuento con ese código")

    new_descuento_data = {
        "codigo": descuento_data.codigo,
        "porcentaje": descuento_data.porcentaje
    }

    new_descuento = descuento_repository.create(db, new_descuento_data)

    return DescuentoMessageResponseSchema(
        message="Descuento creado correctamente",
        descuento=to_descuento_response(new_descuento)
    )
# --------------------------------------------------
# ACTUALIZAR UN DESCUENTO
# --------------------------------------------------

def update_descuento(
    db: Session,
    descuento_id: int,
    descuento_data: DescuentoUpdateSchema
) -> DescuentoMessageResponseSchema:
    updated_fields = {}

    if descuento_data.codigo is not None:
        existing = descuento_repository.get_by_codigo(db, descuento_data.codigo)
        if existing is not None and existing.id_descuento != descuento_id:
            raise HTTPException(status_code=400, detail="Ya existe un descuento con ese código")
        updated_fields["codigo"] = descuento_data.codigo

    if descuento_data.porcentaje is not None:
        updated_fields["porcentaje"] = descuento_data.porcentaje

    updated_descuento = descuento_repository.update(db, descuento_id, updated_fields)

    if updated_descuento is None:
        raise HTTPException(status_code=404, detail="Descuento no encontrado")

    return DescuentoMessageResponseSchema(
        message="Descuento actualizado correctamente",
        descuento=to_descuento_response(updated_descuento)
    )


# --------------------------------------------------
# BORRAR UN DESCUENTO
# --------------------------------------------------

def delete_descuento(
    db: Session,
    descuento_id: int
) -> DescuentoMessageResponseSchema:
    deleted_descuento = descuento_repository.delete(db, descuento_id)

    if deleted_descuento is None:
        raise HTTPException(status_code=404, detail="Descuento no encontrado")

    return DescuentoMessageResponseSchema(
        message="Descuento eliminado correctamente",
        descuento=to_descuento_response(deleted_descuento)
    )