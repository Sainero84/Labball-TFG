from fastapi import HTTPException
from sqlalchemy.orm import Session

from app.repositories import tarifa_repository
from app.schemas.tarifa_schema import (
    TarifaCreateSchema,
    TarifaListResponseSchema,
    TarifaMessageResponseSchema,
    TarifaResponseSchema,
    TarifaUpdateSchema
)


DEFAULT_TARIFAS = [
    {"numero_sesiones": 1, "precio_total": 30.00, "precio_por_sesion": 30.00, "activa": True},
    {"numero_sesiones": 3, "precio_total": 75.00, "precio_por_sesion": 25.00, "activa": True},
    {"numero_sesiones": 5, "precio_total": 100.00, "precio_por_sesion": 20.00, "activa": True},
    {"numero_sesiones": 10, "precio_total": 175.00, "precio_por_sesion": 17.50, "activa": True},
    {"numero_sesiones": 20, "precio_total": 300.00, "precio_por_sesion": 15.00, "activa": True}
]


def calcular_precio_por_sesion(precio_total: float, numero_sesiones: int) -> float:
    return round(float(precio_total) / numero_sesiones, 2)


def schema_to_dict(schema, **kwargs) -> dict:
    if hasattr(schema, "model_dump"):
        return schema.model_dump(**kwargs)

    return schema.dict(**kwargs)


def seed_default_tarifas(db: Session):
    for tarifa_data in DEFAULT_TARIFAS:
        existing = tarifa_repository.get_by_numero_sesiones(
            db,
            tarifa_data["numero_sesiones"]
        )

        if existing is None:
            tarifa_repository.create(db, tarifa_data)


def to_tarifa_response(tarifa) -> TarifaResponseSchema:
    return TarifaResponseSchema(
        id_tarifa=tarifa.id_tarifa,
        numero_sesiones=tarifa.numero_sesiones,
        precio_total=float(tarifa.precio_total),
        precio_por_sesion=float(tarifa.precio_por_sesion),
        activa=tarifa.activa
    )


def get_all_tarifas(db: Session) -> TarifaListResponseSchema:
    tarifas = tarifa_repository.get_all(db)

    return TarifaListResponseSchema(
        tarifas=[to_tarifa_response(tarifa) for tarifa in tarifas]
    )


def get_active_tarifas(db: Session) -> TarifaListResponseSchema:
    tarifas = tarifa_repository.get_all_active(db)

    return TarifaListResponseSchema(
        tarifas=[to_tarifa_response(tarifa) for tarifa in tarifas]
    )


def get_tarifa_by_id(db: Session, tarifa_id: int) -> TarifaResponseSchema:
    tarifa = tarifa_repository.get_by_id(db, tarifa_id)

    if tarifa is None:
        raise HTTPException(status_code=404, detail="Tarifa no encontrada")

    return to_tarifa_response(tarifa)


def create_tarifa(
    db: Session,
    tarifa_data: TarifaCreateSchema
) -> TarifaMessageResponseSchema:
    existing = tarifa_repository.get_by_numero_sesiones(
        db,
        tarifa_data.numero_sesiones
    )

    if existing is not None:
        raise HTTPException(
            status_code=400,
            detail="Ya existe una tarifa para ese numero de sesiones"
        )

    new_tarifa_data = schema_to_dict(tarifa_data)
    new_tarifa_data["precio_por_sesion"] = calcular_precio_por_sesion(
        new_tarifa_data["precio_total"],
        new_tarifa_data["numero_sesiones"]
    )

    tarifa = tarifa_repository.create(db, new_tarifa_data)

    return TarifaMessageResponseSchema(
        message="Tarifa creada correctamente",
        tarifa=to_tarifa_response(tarifa)
    )


def update_tarifa(
    db: Session,
    tarifa_id: int,
    tarifa_data: TarifaUpdateSchema
) -> TarifaMessageResponseSchema:
    updated_fields = schema_to_dict(tarifa_data, exclude_unset=True)
    updated_fields.pop("precio_por_sesion", None)

    if "numero_sesiones" in updated_fields:
        existing = tarifa_repository.get_by_numero_sesiones(
            db,
            updated_fields["numero_sesiones"]
        )

        if existing is not None and existing.id_tarifa != tarifa_id:
            raise HTTPException(
                status_code=400,
                detail="Ya existe una tarifa para ese numero de sesiones"
            )

    tarifa_actual = tarifa_repository.get_by_id(db, tarifa_id)

    if tarifa_actual is None:
        raise HTTPException(status_code=404, detail="Tarifa no encontrada")

    numero_sesiones_final = updated_fields.get(
        "numero_sesiones",
        tarifa_actual.numero_sesiones
    )
    precio_total_final = updated_fields.get(
        "precio_total",
        tarifa_actual.precio_total
    )

    if "numero_sesiones" in updated_fields or "precio_total" in updated_fields:
        updated_fields["precio_por_sesion"] = calcular_precio_por_sesion(
            precio_total_final,
            numero_sesiones_final
        )

    tarifa = tarifa_repository.update(db, tarifa_id, updated_fields)

    if tarifa is None:
        raise HTTPException(status_code=404, detail="Tarifa no encontrada")

    return TarifaMessageResponseSchema(
        message="Tarifa actualizada correctamente",
        tarifa=to_tarifa_response(tarifa)
    )


def delete_tarifa(db: Session, tarifa_id: int) -> TarifaMessageResponseSchema:
    tarifa = tarifa_repository.delete(db, tarifa_id)

    if tarifa is None:
        raise HTTPException(status_code=404, detail="Tarifa no encontrada")

    return TarifaMessageResponseSchema(
        message="Tarifa eliminada correctamente",
        tarifa=to_tarifa_response(tarifa)
    )
