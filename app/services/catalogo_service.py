import secrets
import string

from fastapi import HTTPException
from sqlalchemy.exc import IntegrityError
from sqlalchemy.orm import Session

from app.repositories import (
    descuento_repository,
    entrenador_repository,
    ubicacion_repository
)
from app.schemas.catalogo_schema import (
    EntrenadorListResponseSchema,
    EntrenadorResponseSchema,
    UbicacionCreateSchema,
    UbicacionDeleteResponseSchema,
    UbicacionListResponseSchema,
    UbicacionMessageResponseSchema,
    UbicacionResponseSchema
)
from app.schemas.descuento_schema import (
    DescuentoAdminCreateSchema,
    DescuentoDeleteResponseSchema,
    DescuentoListResponseSchema,
    DescuentoMessageResponseSchema,
    DescuentoResponseSchema,
    DescuentoUpdateSchema
)


def normalize_nombre(nombre: str) -> str:
    """Aplica la logica de negocio necesaria para normalize nombre."""
    return " ".join(nombre.strip().split())


def to_entrenador_response(entrenador) -> EntrenadorResponseSchema:
    """Aplica la logica de negocio necesaria para to entrenador response."""
    return EntrenadorResponseSchema(
        id_entrenador=entrenador.id_entrenador,
        nombre=entrenador.nombre,
        activo=entrenador.activo
    )


def entrenador_nombre_from_usuario(usuario) -> str:
    """Aplica la logica de negocio necesaria para entrenador nombre from usuario."""
    jugador = getattr(usuario, "jugador", None)

    if jugador is not None:
        nombre_completo = " ".join(
            value.strip()
            for value in [jugador.nombre, jugador.apellidos]
            if value and value.strip()
        )

        if nombre_completo:
            return nombre_completo

    return usuario.correo


def sync_entrenador_for_usuario(db: Session, usuario, activo: bool = True):
    """Aplica la logica de negocio necesaria para sync entrenador for usuario."""
    nombre = normalize_nombre(entrenador_nombre_from_usuario(usuario))
    entrenador = entrenador_repository.get_by_usuario_id(db, usuario.id_usuario)

    entrenador_con_mismo_nombre = entrenador_repository.get_by_nombre(db, nombre)

    if (
        entrenador_con_mismo_nombre is not None
        and entrenador_con_mismo_nombre.id_usuario not in [None, usuario.id_usuario]
    ):
        nombre = usuario.correo

    if entrenador is not None:
        entrenador.nombre = nombre
        entrenador.activo = activo
        return entrenador

    entrenador = entrenador_repository.get_by_nombre(db, nombre)

    if entrenador is not None:
        if entrenador.id_usuario is None:
            entrenador.id_usuario = usuario.id_usuario
        entrenador.activo = activo
        return entrenador

    return entrenador_repository.create_without_commit(
        db,
        {
            "id_usuario": usuario.id_usuario,
            "nombre": nombre,
            "activo": activo
        }
    )


def set_entrenador_usuario_activo(db: Session, usuario_id: int, activo: bool):
    """Aplica la logica de negocio necesaria para set entrenador usuario activo."""
    entrenador = entrenador_repository.get_by_usuario_id(db, usuario_id)

    if entrenador is not None:
        entrenador.activo = activo

    return entrenador


def to_ubicacion_response(ubicacion) -> UbicacionResponseSchema:
    """Aplica la logica de negocio necesaria para to ubicacion response."""
    return UbicacionResponseSchema(
        id_ubicacion=ubicacion.id_ubicacion,
        nombre=ubicacion.nombre,
        activo=ubicacion.activo
    )


def to_descuento_response(descuento) -> DescuentoResponseSchema:
    """Aplica la logica de negocio necesaria para to descuento response."""
    return DescuentoResponseSchema(
        id_descuento=descuento.id_descuento,
        codigo=descuento.codigo,
        porcentaje=float(descuento.porcentaje)
    )


def get_all_entrenadores(db: Session) -> EntrenadorListResponseSchema:
    """Aplica la logica de negocio necesaria para get all entrenadores."""
    entrenadores = [
        to_entrenador_response(entrenador)
        for entrenador in entrenador_repository.get_all(db)
    ]

    return EntrenadorListResponseSchema(entrenadores=entrenadores)


def get_all_ubicaciones(db: Session) -> UbicacionListResponseSchema:
    """Aplica la logica de negocio necesaria para get all ubicaciones."""
    ubicaciones = ubicacion_repository.get_all(db)

    return UbicacionListResponseSchema(
        ubicaciones=[
            to_ubicacion_response(ubicacion)
            for ubicacion in ubicaciones
        ]
    )


def create_ubicacion(
    db: Session,
    ubicacion_data: UbicacionCreateSchema
) -> UbicacionMessageResponseSchema:
    """Aplica la logica de negocio necesaria para create ubicacion."""
    nombre = normalize_nombre(ubicacion_data.nombre)

    if ubicacion_repository.get_by_nombre(db, nombre) is not None:
        raise HTTPException(status_code=409, detail="Ya existe una ubicacion con ese nombre")

    ubicacion = ubicacion_repository.create(
        db,
        {
            "nombre": nombre,
            "activo": True
        }
    )

    return UbicacionMessageResponseSchema(
        message="Ubicacion creada correctamente",
        ubicacion=to_ubicacion_response(ubicacion)
    )


def delete_ubicacion(
    db: Session,
    ubicacion_id: int
) -> UbicacionDeleteResponseSchema:
    """Aplica la logica de negocio necesaria para delete ubicacion."""
    try:
        ubicacion = ubicacion_repository.delete(db, ubicacion_id)
    except IntegrityError as exception:
        db.rollback()
        raise HTTPException(
            status_code=409,
            detail="No se puede eliminar la ubicacion porque esta asociada a entrenamientos"
        ) from exception

    if ubicacion is None:
        raise HTTPException(status_code=404, detail="Ubicacion no encontrada")

    return UbicacionDeleteResponseSchema(message="Ubicacion eliminada correctamente")


def get_all_admin_descuentos(db: Session) -> DescuentoListResponseSchema:
    """Aplica la logica de negocio necesaria para get all admin descuentos."""
    descuentos = descuento_repository.get_all(db)

    return DescuentoListResponseSchema(
        descuentos=[
            to_descuento_response(descuento)
            for descuento in descuentos
        ]
    )


def generate_discount_code(db: Session) -> str:
    """Aplica la logica de negocio necesaria para generate discount code."""
    alphabet = string.ascii_uppercase + string.digits

    for _ in range(20):
        code = "LABBALL-" + "".join(secrets.choice(alphabet) for _ in range(8))

        if descuento_repository.get_by_codigo(db, code) is None:
            return code

    raise HTTPException(status_code=500, detail="No se pudo generar un codigo unico")


def create_admin_descuento(
    db: Session,
    descuento_data: DescuentoAdminCreateSchema
) -> DescuentoMessageResponseSchema:
    """Aplica la logica de negocio necesaria para create admin descuento."""
    codigo = (
        descuento_data.codigo.strip().upper()
        if descuento_data.codigo and descuento_data.codigo.strip()
        else generate_discount_code(db)
    )

    if descuento_repository.get_by_codigo(db, codigo) is not None:
        raise HTTPException(status_code=409, detail="Ya existe un descuento con ese codigo")

    descuento = descuento_repository.create(
        db,
        {
            "codigo": codigo,
            "porcentaje": descuento_data.porcentaje
        }
    )

    return DescuentoMessageResponseSchema(
        message="Descuento generado correctamente",
        descuento=to_descuento_response(descuento)
    )


def update_admin_descuento(
    db: Session,
    descuento_id: int,
    descuento_data: DescuentoUpdateSchema
) -> DescuentoMessageResponseSchema:
    """Aplica la logica de negocio necesaria para update admin descuento."""
    descuento = descuento_repository.get_by_id(db, descuento_id)

    if descuento is None:
        raise HTTPException(status_code=404, detail="Descuento no encontrado")

    updated_fields = {}
    fields_set = descuento_data.model_fields_set

    if "codigo" in fields_set:
        if descuento_data.codigo is None:
            raise HTTPException(status_code=400, detail="El codigo no puede estar vacio")

        codigo = descuento_data.codigo.strip().upper()
        existing_descuento = descuento_repository.get_by_codigo(db, codigo)

        if (
            existing_descuento is not None
            and existing_descuento.id_descuento != descuento_id
        ):
            raise HTTPException(status_code=409, detail="Ya existe un descuento con ese codigo")

        updated_fields["codigo"] = codigo

    if "porcentaje" in fields_set:
        updated_fields["porcentaje"] = descuento_data.porcentaje

    if not updated_fields:
        raise HTTPException(status_code=400, detail="No hay cambios para aplicar")

    try:
        descuento = descuento_repository.update(db, descuento_id, updated_fields)
    except IntegrityError as exception:
        db.rollback()
        raise HTTPException(
            status_code=409,
            detail="No se pudo actualizar el descuento porque el codigo ya existe"
        ) from exception

    return DescuentoMessageResponseSchema(
        message="Descuento actualizado correctamente",
        descuento=to_descuento_response(descuento)
    )


def delete_admin_descuento(
    db: Session,
    descuento_id: int
) -> DescuentoDeleteResponseSchema:
    """Aplica la logica de negocio necesaria para delete admin descuento."""
    try:
        descuento = descuento_repository.delete(db, descuento_id)
    except IntegrityError as exception:
        db.rollback()
        raise HTTPException(
            status_code=409,
            detail="No se puede eliminar el descuento porque esta asociado a reservas"
        ) from exception

    if descuento is None:
        raise HTTPException(status_code=404, detail="Descuento no encontrado")

    return DescuentoDeleteResponseSchema(message="Descuento eliminado correctamente")
