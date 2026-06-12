from fastapi import HTTPException
from sqlalchemy.orm import Session

from app.repositories import (
    entrenamiento_repository,
    entrenador_repository,
    inscripcion_repository,
    jugador_repository,
    ubicacion_repository,
    usuario_repository
)

from app.schemas.entrenamiento_schema import (
    EntrenamientoCreateSchema,
    EntrenamientoUpdateSchema,
    EntrenamientoResponseSchema,
    EntrenamientoListResponseSchema,
    EntrenamientoMessageResponseSchema
)


def get_entrenamiento_inscripcion_id(entrenamiento) -> int | None:
    """Aplica la logica de negocio necesaria para get entrenamiento inscripcion id."""
    return entrenamiento.id_inscripcion


def get_entrenamiento_usuario_id(db: Session, entrenamiento) -> int | None:
    """Aplica la logica de negocio necesaria para get entrenamiento usuario id."""
    inscripcion_id = get_entrenamiento_inscripcion_id(entrenamiento)

    if inscripcion_id is not None:
        inscripcion = inscripcion_repository.get_by_id(db, inscripcion_id)
        return inscripcion.id_usuario if inscripcion is not None else None

    if entrenamiento.id_jugador is not None:
        jugador = jugador_repository.get_by_id(db, entrenamiento.id_jugador)
        return jugador.id_usuario if jugador is not None else None

    return None


def get_entrenamiento_jugador_id(db: Session, entrenamiento) -> int | None:
    """Aplica la logica de negocio necesaria para get entrenamiento jugador id."""
    if entrenamiento.id_jugador is not None:
        return entrenamiento.id_jugador

    usuario_id = get_entrenamiento_usuario_id(db, entrenamiento)

    if usuario_id is None:
        return None

    jugador = jugador_repository.get_by_usuario_id(db, usuario_id)
    return jugador.id_jugador if jugador is not None else None


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


def resolve_catalogos_entrenamiento(
    db: Session,
    id_entrenador: int,
    id_ubicacion: int
):
    """Aplica la logica de negocio necesaria para resolve catalogos entrenamiento."""
    entrenador = entrenador_repository.get_by_id(db, id_entrenador)

    if entrenador is None:
        raise HTTPException(status_code=404, detail="Entrenador no encontrado")

    if not entrenador.activo:
        raise HTTPException(status_code=400, detail="El entrenador no esta activo")

    ubicacion = ubicacion_repository.get_by_id(db, id_ubicacion)

    if ubicacion is None:
        raise HTTPException(status_code=404, detail="Ubicacion no encontrada")

    if not ubicacion.activo:
        raise HTTPException(status_code=400, detail="La ubicacion no esta activa")

    return entrenador, ubicacion


def to_entrenamiento_response(
    db: Session,
    entrenamiento
) -> EntrenamientoResponseSchema:
    """Aplica la logica de negocio necesaria para to entrenamiento response."""
    id_inscripcion = get_entrenamiento_inscripcion_id(entrenamiento)

    return EntrenamientoResponseSchema(
        id_entrenamiento=entrenamiento.id_entrenamiento,
        id_entrenador=entrenamiento.id_entrenador,
        nombre_entrenador=get_entrenamiento_nombre_entrenador(entrenamiento),
        id_ubicacion=entrenamiento.id_ubicacion,
        ubicacion=get_entrenamiento_ubicacion(entrenamiento),
        hora_inicio=entrenamiento.hora_inicio,
        hora_fin=entrenamiento.hora_fin,
        id_inscripcion=id_inscripcion,
        id_jugador=get_entrenamiento_jugador_id(db, entrenamiento),
        id_usuario=get_entrenamiento_usuario_id(db, entrenamiento),
        id_reserva=id_inscripcion
    )


def resolve_inscripcion_for_entrenamiento(
    db: Session,
    id_inscripcion: int | None,
    id_reserva: int | None
):
    """Aplica la logica de negocio necesaria para resolve inscripcion for entrenamiento."""
    resolved_id = id_inscripcion or id_reserva

    if resolved_id is None:
        raise HTTPException(
            status_code=400,
            detail="Debes indicar id_inscripcion o id_reserva"
        )

    inscripcion = inscripcion_repository.get_by_id(db, resolved_id)

    if inscripcion is None:
        raise HTTPException(status_code=404, detail="Inscripcion no encontrada")

    return inscripcion


def resolve_legacy_jugador_id(
    db: Session,
    inscripcion,
    requested_jugador_id: int | None
) -> int | None:
    """Aplica la logica de negocio necesaria para resolve legacy jugador id."""
    if requested_jugador_id is None:
        jugador = jugador_repository.get_by_usuario_id(
            db,
            inscripcion.id_usuario
        )
        return jugador.id_jugador if jugador is not None else None

    jugador = jugador_repository.get_by_id(db, requested_jugador_id)

    if jugador is None:
        raise HTTPException(status_code=404, detail="Jugador no encontrado")

    if jugador.id_usuario != inscripcion.id_usuario:
        raise HTTPException(
            status_code=400,
            detail="El jugador no pertenece al usuario responsable de la inscripcion"
        )

    return jugador.id_jugador


def assert_usuario_matches_inscripcion(
    requested_usuario_id: int | None,
    inscripcion
):
    """Aplica la logica de negocio necesaria para assert usuario matches inscripcion."""
    if (
        requested_usuario_id is not None
        and requested_usuario_id != inscripcion.id_usuario
    ):
        raise HTTPException(
            status_code=400,
            detail="El id_usuario no coincide con la inscripcion"
        )


def get_all_entrenamientos(
    db: Session
) -> EntrenamientoListResponseSchema:
    """Aplica la logica de negocio necesaria para get all entrenamientos."""
    entrenamientos = entrenamiento_repository.get_all(db)

    return EntrenamientoListResponseSchema(
        entrenamientos=[
            to_entrenamiento_response(db, entrenamiento)
            for entrenamiento in entrenamientos
        ]
    )


def get_entrenamiento_by_id(
    db: Session,
    entrenamiento_id: int
) -> EntrenamientoResponseSchema:
    """Aplica la logica de negocio necesaria para get entrenamiento by id."""
    entrenamiento = entrenamiento_repository.get_by_id(db, entrenamiento_id)

    if entrenamiento is None:
        raise HTTPException(
            status_code=404,
            detail="Entrenamiento no encontrado"
        )

    return to_entrenamiento_response(db, entrenamiento)


def get_entrenamientos_by_user(
    db: Session,
    current_user
) -> EntrenamientoListResponseSchema:
    """Aplica la logica de negocio necesaria para get entrenamientos by user."""
    entrenamientos = entrenamiento_repository.get_by_usuario_id(
        db,
        current_user.id_usuario
    )

    return EntrenamientoListResponseSchema(
        entrenamientos=[
            to_entrenamiento_response(db, entrenamiento)
            for entrenamiento in entrenamientos
        ]
    )


def get_entrenamiento_by_user(
    db: Session,
    entrenamiento_id: int,
    current_user
) -> EntrenamientoResponseSchema:
    """Aplica la logica de negocio necesaria para get entrenamiento by user."""
    entrenamiento = entrenamiento_repository.get_by_id_and_usuario_id(
        db,
        entrenamiento_id,
        current_user.id_usuario
    )

    if entrenamiento is None:
        raise HTTPException(
            status_code=404,
            detail="Entrenamiento no encontrado"
        )

    return to_entrenamiento_response(db, entrenamiento)


def create_entrenamiento(
    db: Session,
    entrenamiento_data: EntrenamientoCreateSchema
) -> EntrenamientoMessageResponseSchema:
    """Aplica la logica de negocio necesaria para create entrenamiento."""
    inscripcion = resolve_inscripcion_for_entrenamiento(
        db,
        entrenamiento_data.id_inscripcion,
        entrenamiento_data.id_reserva
    )
    assert_usuario_matches_inscripcion(entrenamiento_data.id_usuario, inscripcion)

    usuario = usuario_repository.get_by_id(db, inscripcion.id_usuario)

    if usuario is None:
        raise HTTPException(
            status_code=404,
            detail="Usuario responsable no encontrado"
        )

    if entrenamiento_data.hora_fin <= entrenamiento_data.hora_inicio:
        raise HTTPException(
            status_code=400,
            detail="La hora de fin debe ser posterior a la hora de inicio"
        )

    id_jugador = resolve_legacy_jugador_id(
        db,
        inscripcion,
        entrenamiento_data.id_jugador
    )
    entrenador, ubicacion = resolve_catalogos_entrenamiento(
        db,
        entrenamiento_data.id_entrenador,
        entrenamiento_data.id_ubicacion
    )

    new_entrenamiento_data = {
        "id_entrenador": entrenador.id_entrenador,
        "id_ubicacion": ubicacion.id_ubicacion,
        "hora_inicio": entrenamiento_data.hora_inicio,
        "hora_fin": entrenamiento_data.hora_fin,
        "id_inscripcion": inscripcion.id_inscripcion,
        "id_jugador": id_jugador
    }

    new_entrenamiento = entrenamiento_repository.create(
        db,
        new_entrenamiento_data
    )

    return EntrenamientoMessageResponseSchema(
        message="Entrenamiento creado correctamente",
        entrenamiento=to_entrenamiento_response(db, new_entrenamiento)
    )


def update_entrenamiento(
    db: Session,
    entrenamiento_id: int,
    entrenamiento_data: EntrenamientoUpdateSchema
) -> EntrenamientoMessageResponseSchema:
    """Aplica la logica de negocio necesaria para update entrenamiento."""
    entrenamiento_actual = entrenamiento_repository.get_by_id(
        db,
        entrenamiento_id
    )

    if entrenamiento_actual is None:
        raise HTTPException(
            status_code=404,
            detail="Entrenamiento no encontrado"
        )

    updated_fields = {}

    if entrenamiento_data.id_entrenador is not None:
        entrenador = entrenador_repository.get_by_id(
            db,
            entrenamiento_data.id_entrenador
        )

        if entrenador is None:
            raise HTTPException(status_code=404, detail="Entrenador no encontrado")

        if not entrenador.activo:
            raise HTTPException(status_code=400, detail="El entrenador no esta activo")

        updated_fields["id_entrenador"] = entrenador.id_entrenador

    if entrenamiento_data.id_ubicacion is not None:
        ubicacion = ubicacion_repository.get_by_id(
            db,
            entrenamiento_data.id_ubicacion
        )

        if ubicacion is None:
            raise HTTPException(status_code=404, detail="Ubicacion no encontrada")

        if not ubicacion.activo:
            raise HTTPException(status_code=400, detail="La ubicacion no esta activa")

        updated_fields["id_ubicacion"] = ubicacion.id_ubicacion

    if entrenamiento_data.hora_inicio is not None:
        updated_fields["hora_inicio"] = entrenamiento_data.hora_inicio

    if entrenamiento_data.hora_fin is not None:
        updated_fields["hora_fin"] = entrenamiento_data.hora_fin

    nueva_inscripcion_id = (
        entrenamiento_data.id_inscripcion
        or entrenamiento_data.id_reserva
    )

    if nueva_inscripcion_id is not None:
        inscripcion = resolve_inscripcion_for_entrenamiento(
            db,
            entrenamiento_data.id_inscripcion,
            entrenamiento_data.id_reserva
        )
        assert_usuario_matches_inscripcion(
            entrenamiento_data.id_usuario,
            inscripcion
        )

        updated_fields["id_inscripcion"] = inscripcion.id_inscripcion
        updated_fields["id_jugador"] = resolve_legacy_jugador_id(
            db,
            inscripcion,
            entrenamiento_data.id_jugador
        )
    else:
        if entrenamiento_data.id_jugador is not None:
            jugador = jugador_repository.get_by_id(
                db,
                entrenamiento_data.id_jugador
            )

            if jugador is None:
                raise HTTPException(
                    status_code=404,
                    detail="Jugador no encontrado"
                )

            updated_fields["id_jugador"] = entrenamiento_data.id_jugador

    hora_inicio_final = updated_fields.get(
        "hora_inicio",
        entrenamiento_actual.hora_inicio
    )

    hora_fin_final = updated_fields.get(
        "hora_fin",
        entrenamiento_actual.hora_fin
    )

    if hora_fin_final <= hora_inicio_final:
        raise HTTPException(
            status_code=400,
            detail="La hora de fin debe ser posterior a la hora de inicio"
        )

    updated_entrenamiento = entrenamiento_repository.update(
        db,
        entrenamiento_id,
        updated_fields
    )

    return EntrenamientoMessageResponseSchema(
        message="Entrenamiento actualizado correctamente",
        entrenamiento=to_entrenamiento_response(db, updated_entrenamiento)
    )


def delete_entrenamiento(
    db: Session,
    entrenamiento_id: int
) -> EntrenamientoMessageResponseSchema:
    """Aplica la logica de negocio necesaria para delete entrenamiento."""
    entrenamiento = entrenamiento_repository.get_by_id(db, entrenamiento_id)

    if entrenamiento is None:
        raise HTTPException(
            status_code=404,
            detail="Entrenamiento no encontrado"
        )

    response = to_entrenamiento_response(db, entrenamiento)
    deleted_entrenamiento = entrenamiento_repository.delete(
        db,
        entrenamiento_id
    )

    if deleted_entrenamiento is None:
        raise HTTPException(
            status_code=404,
            detail="Entrenamiento no encontrado"
        )

    return EntrenamientoMessageResponseSchema(
        message="Entrenamiento eliminado correctamente",
        entrenamiento=response
    )
