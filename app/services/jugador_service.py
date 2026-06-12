import os
from urllib.parse import urlparse

from fastapi import HTTPException
from sqlalchemy.orm import Session

from app.repositories import jugador_repository, usuario_repository
from app.schemas.jugador_schema import (
    JugadorCreateSchema,
    JugadorEntrenadorAsignadoSchema,
    JugadorListResponseSchema,
    JugadorMessageResponseSchema,
    JugadorResponseSchema,
    JugadorUbicacionAsignadaSchema,
    JugadorUpdateSchema
)


# --------------------------------------------------
# FUNCIÓN AUXILIAR
# --------------------------------------------------

def normalize_static_url(url: str | None) -> str | None:
    """Aplica la logica de negocio necesaria para normalize static url."""
    if url is None:
        return None

    parsed_url = urlparse(url)

    if parsed_url.path.startswith("/static/"):
        public_base_url = os.getenv("PUBLIC_BASE_URL")

        if public_base_url:
            return public_base_url.rstrip("/") + parsed_url.path

    return url


def get_entrenamiento_nombre_entrenador(entrenamiento) -> str:
    """Aplica la logica de negocio necesaria para get entrenamiento nombre entrenador."""
    entrenador = getattr(entrenamiento, "entrenador", None)

    if entrenador is not None:
        return entrenador.nombre

    return ""


def get_entrenamiento_ubicacion(entrenamiento) -> str:
    """Aplica la logica de negocio necesaria para get entrenamiento ubicacion."""
    ubicacion = getattr(entrenamiento, "ubicacion_catalogo", None)

    if ubicacion is not None:
        return ubicacion.nombre

    return ""


def get_jugador_catalogos(db: Session, jugador):
    """Aplica la logica de negocio necesaria para get jugador catalogos."""
    entrenamientos = jugador_repository.get_entrenamientos_catalogo_by_jugador(
        db,
        jugador
    )
    entrenadores: list[JugadorEntrenadorAsignadoSchema] = []
    ubicaciones: list[JugadorUbicacionAsignadaSchema] = []
    entrenador_ids_vistos = set()
    ubicacion_ids_vistos = set()

    for entrenamiento in entrenamientos:
        if entrenamiento.id_entrenador not in entrenador_ids_vistos:
            entrenador_ids_vistos.add(entrenamiento.id_entrenador)
            entrenadores.append(
                JugadorEntrenadorAsignadoSchema(
                    id_entrenador=entrenamiento.id_entrenador,
                    nombre=get_entrenamiento_nombre_entrenador(entrenamiento)
                )
            )

        if entrenamiento.id_ubicacion not in ubicacion_ids_vistos:
            ubicacion_ids_vistos.add(entrenamiento.id_ubicacion)
            ubicaciones.append(
                JugadorUbicacionAsignadaSchema(
                    id_ubicacion=entrenamiento.id_ubicacion,
                    nombre=get_entrenamiento_ubicacion(entrenamiento)
                )
            )

    return entrenadores, ubicaciones


def to_jugador_response(db: Session, jugador) -> JugadorResponseSchema:
    """Aplica la logica de negocio necesaria para to jugador response."""
    foto_perfil_url = None

    if jugador.usuario is not None:
        foto_perfil_url = normalize_static_url(jugador.usuario.foto_perfil_url)

    entrenadores, ubicaciones = get_jugador_catalogos(db, jugador)
    entrenador_principal = entrenadores[0] if entrenadores else None
    ubicacion_principal = ubicaciones[0] if ubicaciones else None

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
        foto_perfil_url=foto_perfil_url,
        id_entrenador=(
            entrenador_principal.id_entrenador
            if entrenador_principal is not None else None
        ),
        nombre_entrenador=(
            entrenador_principal.nombre
            if entrenador_principal is not None else None
        ),
        id_ubicacion=(
            ubicacion_principal.id_ubicacion
            if ubicacion_principal is not None else None
        ),
        ubicacion=(
            ubicacion_principal.nombre
            if ubicacion_principal is not None else None
        ),
        entrenadores=entrenadores,
        ubicaciones=ubicaciones
    )


# --------------------------------------------------
# OBTENER TODOS LOS JUGADORES
# --------------------------------------------------

def get_all_jugadores(db: Session) -> JugadorListResponseSchema:
    """Aplica la logica de negocio necesaria para get all jugadores."""
    jugadores = jugador_repository.get_all(db)

    return JugadorListResponseSchema(
        jugadores=[to_jugador_response(db, jugador) for jugador in jugadores]
    )


# --------------------------------------------------
# OBTENER UN JUGADOR POR ID
# --------------------------------------------------

def get_jugador_by_id(db: Session, jugador_id: int) -> JugadorResponseSchema:
    """Aplica la logica de negocio necesaria para get jugador by id."""
    jugador = jugador_repository.get_by_id(db, jugador_id)

    if jugador is None:
        raise HTTPException(status_code=404, detail="Jugador no encontrado")

    return to_jugador_response(db, jugador)


def get_jugador_by_user(db: Session, current_user) -> JugadorResponseSchema:
    """Aplica la logica de negocio necesaria para get jugador by user."""
    jugador = jugador_repository.get_by_usuario_id(db, current_user.id_usuario)

    if jugador is None:
        raise HTTPException(status_code=404, detail="Jugador no encontrado")

    return to_jugador_response(db, jugador)


# --------------------------------------------------
# CREAR UN JUGADOR
# --------------------------------------------------

def create_jugador(
    db: Session,
    jugador_data: JugadorCreateSchema
) -> JugadorMessageResponseSchema:
    """Aplica la logica de negocio necesaria para create jugador."""
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
        jugador=to_jugador_response(db, new_jugador)
    )


# --------------------------------------------------
# ACTUALIZAR UN JUGADOR
# --------------------------------------------------

def update_jugador(
    db: Session,
    jugador_id: int,
    jugador_data: JugadorUpdateSchema
) -> JugadorMessageResponseSchema:
    """Aplica la logica de negocio necesaria para update jugador."""
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
        jugador=to_jugador_response(db, updated_jugador)
    )


# --------------------------------------------------
# BORRAR UN JUGADOR
# --------------------------------------------------

def delete_jugador(
    db: Session,
    jugador_id: int
) -> JugadorMessageResponseSchema:
    """Aplica la logica de negocio necesaria para delete jugador."""
    deleted_jugador = jugador_repository.delete(db, jugador_id)

    if deleted_jugador is None:
        raise HTTPException(status_code=404, detail="Jugador no encontrado")

    return JugadorMessageResponseSchema(
        message="Jugador eliminado correctamente",
        jugador=to_jugador_response(db, deleted_jugador)
    )
