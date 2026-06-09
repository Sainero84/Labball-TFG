import os
from urllib.parse import urlparse

from fastapi import HTTPException
from sqlalchemy.orm import Session

from app.repositories import jugador_repository, usuario_repository
from app.schemas.jugador_schema import (
    JugadorCreateSchema,
    JugadorUpdateSchema,
    JugadorResponseSchema,
    JugadorListResponseSchema,
    JugadorMessageResponseSchema
)


# --------------------------------------------------
# FUNCIÓN AUXILIAR
# --------------------------------------------------

def normalize_static_url(url: str | None) -> str | None:
    if url is None:
        return None

    parsed_url = urlparse(url)

    if parsed_url.path.startswith("/static/"):
        public_base_url = os.getenv("PUBLIC_BASE_URL")

        if public_base_url:
            return public_base_url.rstrip("/") + parsed_url.path

    return url


def to_jugador_response(jugador) -> JugadorResponseSchema:
    foto_perfil_url = None

    if jugador.usuario is not None:
        foto_perfil_url = normalize_static_url(jugador.usuario.foto_perfil_url)

    return JugadorResponseSchema(
        id_jugador=jugador.id_jugador,
        id_usuario=jugador.id_usuario,
        nombre=jugador.nombre,
        apellidos=jugador.apellidos,
        peso=float(jugador.peso) if jugador.peso is not None else None,
        altura=float(jugador.altura) if jugador.altura is not None else None,
        posicion=jugador.posicion,
        tiro=jugador.tiro,
        fisico=jugador.fisico,
        bote=jugador.bote,
        pase=jugador.pase,
        defensa=jugador.defensa,
        velocidad=jugador.velocidad,
        foto_perfil_url=foto_perfil_url
    )


# --------------------------------------------------
# OBTENER TODOS LOS JUGADORES
# --------------------------------------------------

def get_all_jugadores(db: Session) -> JugadorListResponseSchema:
    jugadores = jugador_repository.get_all(db)

    return JugadorListResponseSchema(
        jugadores=[to_jugador_response(jugador) for jugador in jugadores]
    )


# --------------------------------------------------
# OBTENER UN JUGADOR POR ID
# --------------------------------------------------

def get_jugador_by_id(db: Session, jugador_id: int) -> JugadorResponseSchema:
    jugador = jugador_repository.get_by_id(db, jugador_id)

    if jugador is None:
        raise HTTPException(status_code=404, detail="Jugador no encontrado")

    return to_jugador_response(jugador)


def get_jugador_by_user(db: Session, current_user) -> JugadorResponseSchema:
    jugador = jugador_repository.get_by_usuario_id(db, current_user.id_usuario)

    if jugador is None:
        raise HTTPException(status_code=404, detail="Jugador no encontrado")

    return to_jugador_response(jugador)


# --------------------------------------------------
# CREAR UN JUGADOR
# --------------------------------------------------

def create_jugador(
    db: Session,
    jugador_data: JugadorCreateSchema
) -> JugadorMessageResponseSchema:
    usuario = usuario_repository.get_by_id(db, jugador_data.id_usuario)

    if usuario is None:
        raise HTTPException(status_code=404, detail="Usuario no encontrado")

    existing_jugador = jugador_repository.get_by_usuario_id(
        db,
        jugador_data.id_usuario
    )

    if existing_jugador is not None:
        raise HTTPException(
            status_code=400,
            detail="El usuario ya tiene un jugador asociado"
        )

    new_jugador_data = {
        "id_usuario": jugador_data.id_usuario,
        "nombre": jugador_data.nombre,
        "apellidos": jugador_data.apellidos,
        "peso": jugador_data.peso,
        "altura": jugador_data.altura,
        "posicion": jugador_data.posicion,
        "tiro": jugador_data.tiro,
        "fisico": jugador_data.fisico,
        "bote": jugador_data.bote,
        "pase": jugador_data.pase,
        "defensa": jugador_data.defensa,
        "velocidad": jugador_data.velocidad
    }

    new_jugador = jugador_repository.create(db, new_jugador_data)

    return JugadorMessageResponseSchema(
        message="Jugador creado correctamente",
        jugador=to_jugador_response(new_jugador)
    )


# --------------------------------------------------
# ACTUALIZAR UN JUGADOR
# --------------------------------------------------

def update_jugador(
    db: Session,
    jugador_id: int,
    jugador_data: JugadorUpdateSchema
) -> JugadorMessageResponseSchema:
    updated_fields = {}

    id_usuario = getattr(jugador_data, "id_usuario", None)

    if id_usuario is not None:
        usuario = usuario_repository.get_by_id(db, id_usuario)

        if usuario is None:
            raise HTTPException(status_code=404, detail="Usuario no encontrado")

        existing_jugador = jugador_repository.get_by_usuario_id(
            db,
            id_usuario
        )

        if existing_jugador is not None and existing_jugador.id_jugador != jugador_id:
            raise HTTPException(
                status_code=400,
                detail="El usuario ya tiene un jugador asociado"
            )

        updated_fields["id_usuario"] = id_usuario

    for field_name in [
        "nombre",
        "apellidos",
        "peso",
        "altura",
        "posicion",
        "tiro",
        "fisico",
        "bote",
        "pase",
        "defensa",
        "velocidad"
    ]:
        value = getattr(jugador_data, field_name, None)

        if value is not None:
            updated_fields[field_name] = value

    updated_jugador = jugador_repository.update(db, jugador_id, updated_fields)

    if updated_jugador is None:
        raise HTTPException(status_code=404, detail="Jugador no encontrado")

    return JugadorMessageResponseSchema(
        message="Jugador actualizado correctamente",
        jugador=to_jugador_response(updated_jugador)
    )


# --------------------------------------------------
# BORRAR UN JUGADOR
# --------------------------------------------------

def delete_jugador(
    db: Session,
    jugador_id: int
) -> JugadorMessageResponseSchema:
    deleted_jugador = jugador_repository.delete(db, jugador_id)

    if deleted_jugador is None:
        raise HTTPException(status_code=404, detail="Jugador no encontrado")

    return JugadorMessageResponseSchema(
        message="Jugador eliminado correctamente",
        jugador=to_jugador_response(deleted_jugador)
    )
