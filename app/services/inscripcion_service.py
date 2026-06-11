from fastapi import HTTPException
from sqlalchemy.orm import Session

from app.repositories import (
    descuento_repository,
    entrenamiento_repository,
    entrenador_repository,
    inscripcion_repository,
    inscripcion_semana_repository,
    jugador_repository,
    semana_repository,
    tarifa_repository,
    ubicacion_repository
)
from app.schemas.inscripcion_schema import (
    ReservaEntrenamientosAsignadosResponseSchema,
    ReservaEntrenamientosAsignarSchema,
    ReservaAdminListItemSchema,
    ReservaAdminListResponseSchema,
    ReservaCreateSchema,
    ReservaListResponseSchema,
    ReservaMessageResponseSchema,
    ReservaPreviewRequestSchema,
    ReservaPreviewResponseSchema,
    ReservaResponseSchema
)
from app.schemas.entrenamiento_schema import (
    EntrenamientoListResponseSchema,
    EntrenamientoResponseSchema
)


def calcular_precio_final(precio_sin_descuento: float, porcentaje_descuento: float | None) -> float:
    if porcentaje_descuento is None:
        return round(precio_sin_descuento, 2)

    descuento_aplicado = precio_sin_descuento * (porcentaje_descuento / 100)
    return round(precio_sin_descuento - descuento_aplicado, 2)


def calcular_descuento_aplicado(precio_sin_descuento: float, porcentaje_descuento: float | None) -> float:
    if porcentaje_descuento is None:
        return 0.0

    return round(precio_sin_descuento * (porcentaje_descuento / 100), 2)


def resolve_tarifa(
    db: Session,
    id_tarifa: int | None = None,
    numero_sesiones: int | None = None
):
    if id_tarifa is None and numero_sesiones is None:
        raise HTTPException(
            status_code=400,
            detail="Debes indicar id_tarifa o numero_sesiones"
        )

    if id_tarifa is not None:
        tarifa = tarifa_repository.get_by_id(db, id_tarifa)

        if tarifa is None:
            raise HTTPException(status_code=404, detail="Tarifa no encontrada")

        if (
            numero_sesiones is not None
            and tarifa.numero_sesiones != numero_sesiones
        ):
            raise HTTPException(
                status_code=400,
                detail="id_tarifa y numero_sesiones no coinciden"
            )
    else:
        tarifa = tarifa_repository.get_by_numero_sesiones(db, numero_sesiones)

        if tarifa is None:
            raise HTTPException(
                status_code=404,
                detail="No existe una tarifa activa para ese numero de sesiones"
            )

    if not tarifa.activa:
        raise HTTPException(
            status_code=404,
            detail="No existe una tarifa activa para ese numero de sesiones"
        )

    return tarifa


def to_reserva_response(db: Session, reserva) -> ReservaResponseSchema:
    relaciones = inscripcion_semana_repository.get_by_inscripcion_id(
        db,
        reserva.id_inscripcion
    )
    descuento = None

    if reserva.id_descuento is not None:
        descuento = descuento_repository.get_by_id(db, reserva.id_descuento)

    jugador = jugador_repository.get_by_usuario_id(db, reserva.id_usuario)

    return ReservaResponseSchema(
        id_reserva=reserva.id_inscripcion,
        id_usuario=reserva.id_usuario,
        id_jugador=jugador.id_jugador if jugador is not None else None,
        id_tarifa=reserva.id_tarifa,
        numero_sesiones=reserva.numero_sesiones,
        nombre=reserva.nombre,
        apellidos=reserva.apellidos,
        dni=reserva.dni,
        fecha_nacimiento=reserva.fecha_nacimiento,
        correo=reserva.correo,
        telefono=reserva.telefono,
        club=reserva.club,
        categoria=reserva.categoria,
        codigo_descuento=descuento.codigo if descuento is not None else None,
        semanas=[relacion.id_semana for relacion in relaciones],
        precio_sin_descuento=float(reserva.precio_sin_descuento),
        descuento_aplicado=float(reserva.descuento_aplicado),
        precio_final=float(reserva.precio_final),
        pagado=reserva.pagado
    )


def to_reserva_admin_list_item(db: Session, reserva) -> ReservaAdminListItemSchema:
    entrenamientos_asignados = entrenamiento_repository.count_by_reserva_id(
        db,
        reserva.id_inscripcion
    )
    asignacion_completa = entrenamientos_asignados >= reserva.numero_sesiones

    return ReservaAdminListItemSchema(
        id_reserva=reserva.id_inscripcion,
        nombre=reserva.nombre,
        apellidos=reserva.apellidos,
        correo=reserva.correo,
        numero_sesiones=reserva.numero_sesiones,
        pagado=reserva.pagado,
        entrenamientos_asignados=entrenamientos_asignados,
        asignacion_completa=asignacion_completa,
        tiene_entrenamientos=asignacion_completa
    )


def get_entrenamiento_nombre_entrenador(entrenamiento) -> str:
    entrenador = getattr(entrenamiento, "entrenador", None)

    if entrenador is not None:
        return entrenador.nombre

    return entrenamiento.nombre_entrenador


def get_entrenamiento_ubicacion(entrenamiento) -> str:
    ubicacion = getattr(entrenamiento, "ubicacion_catalogo", None)

    if ubicacion is not None:
        return ubicacion.nombre

    return entrenamiento.ubicacion


def resolve_catalogos_entrenamiento(
    db: Session,
    id_entrenador: int,
    id_ubicacion: int
):
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


def to_entrenamiento_response(db: Session, entrenamiento) -> EntrenamientoResponseSchema:
    id_inscripcion = entrenamiento.id_inscripcion or entrenamiento.id_reserva
    id_usuario = entrenamiento.id_usuario

    if id_usuario is None and id_inscripcion is not None:
        inscripcion = inscripcion_repository.get_by_id(db, id_inscripcion)
        id_usuario = inscripcion.id_usuario if inscripcion is not None else None

    id_jugador = entrenamiento.id_jugador

    if id_jugador is None and id_usuario is not None:
        jugador = jugador_repository.get_by_usuario_id(db, id_usuario)
        id_jugador = jugador.id_jugador if jugador is not None else None

    return EntrenamientoResponseSchema(
        id_entrenamiento=entrenamiento.id_entrenamiento,
        id_entrenador=entrenamiento.id_entrenador,
        nombre_entrenador=get_entrenamiento_nombre_entrenador(entrenamiento),
        id_ubicacion=entrenamiento.id_ubicacion,
        ubicacion=get_entrenamiento_ubicacion(entrenamiento),
        hora_inicio=entrenamiento.hora_inicio,
        hora_fin=entrenamiento.hora_fin,
        id_inscripcion=id_inscripcion,
        id_jugador=id_jugador,
        id_usuario=id_usuario,
        id_reserva=id_inscripcion
    )

def create_reserva(
    db: Session,
    reserva_data: ReservaCreateSchema,
    current_user
) -> ReservaMessageResponseSchema:
    descuento = None
    porcentaje_descuento = None

    if reserva_data.codigo_descuento is not None:
        descuento = descuento_repository.get_by_codigo(db, reserva_data.codigo_descuento)

        if descuento is None:
            raise HTTPException(status_code=404, detail="Descuento no encontrado")

        porcentaje_descuento = float(descuento.porcentaje)

    for id_semana in reserva_data.semanas:
        semana = semana_repository.get_by_id(db, id_semana)
        if semana is None:
            raise HTTPException(status_code=404, detail=f"Semana {id_semana} no encontrada")

    tarifa = resolve_tarifa(
        db,
        id_tarifa=reserva_data.id_tarifa,
        numero_sesiones=reserva_data.numero_sesiones
    )
    precio_sin_descuento = float(tarifa.precio_total)
    descuento_aplicado = calcular_descuento_aplicado(
        precio_sin_descuento,
        porcentaje_descuento
    )
    precio_final = calcular_precio_final(
        precio_sin_descuento,
        porcentaje_descuento
    )

    new_reserva = inscripcion_repository.create(
        db,
        {
            "id_usuario": current_user.id_usuario,
            "id_tarifa": tarifa.id_tarifa,
            "numero_sesiones": tarifa.numero_sesiones,
            "nombre": reserva_data.nombre,
            "apellidos": reserva_data.apellidos,
            "dni": reserva_data.dni,
            "fecha_nacimiento": reserva_data.fecha_nacimiento,
            "correo": reserva_data.correo,
            "telefono": reserva_data.telefono,
            "club": reserva_data.club,
            "categoria": reserva_data.categoria,
            "precio_sin_descuento": precio_sin_descuento,
            "descuento_aplicado": descuento_aplicado,
            "precio_final": precio_final,
            "pagado": False,
            "id_descuento": descuento.id_descuento if descuento is not None else None
        }
    )

    for id_semana in reserva_data.semanas:
        inscripcion_semana_repository.create(
            db,
            {
                "id_inscripcion": new_reserva.id_inscripcion,
                "id_semana": id_semana
            }
        )

    return ReservaMessageResponseSchema(
        message="Reserva creada correctamente",
        reserva=to_reserva_response(db, new_reserva)
    )


def preview_reserva_precio(
    db: Session,
    preview_data: ReservaPreviewRequestSchema
) -> ReservaPreviewResponseSchema:
    tarifa = resolve_tarifa(
        db,
        id_tarifa=preview_data.id_tarifa,
        numero_sesiones=preview_data.numero_sesiones
    )

    descuento = None
    porcentaje_descuento = None

    if preview_data.codigo_descuento is not None:
        descuento = descuento_repository.get_by_codigo(
            db,
            preview_data.codigo_descuento
        )

        if descuento is None:
            raise HTTPException(status_code=404, detail="Descuento no encontrado")

        porcentaje_descuento = float(descuento.porcentaje)

    precio_sin_descuento = float(tarifa.precio_total)
    descuento_aplicado = calcular_descuento_aplicado(
        precio_sin_descuento,
        porcentaje_descuento
    )
    precio_final = calcular_precio_final(
        precio_sin_descuento,
        porcentaje_descuento
    )

    return ReservaPreviewResponseSchema(
        id_tarifa=tarifa.id_tarifa,
        numero_sesiones=tarifa.numero_sesiones,
        precio_sin_descuento=precio_sin_descuento,
        descuento_aplicado=descuento_aplicado,
        precio_final=precio_final,
        codigo_descuento=descuento.codigo if descuento is not None else None
    )


def get_reservas_by_user(db: Session, current_user) -> ReservaListResponseSchema:
    reservas = inscripcion_repository.get_by_usuario_id(db, current_user.id_usuario)

    return ReservaListResponseSchema(
        reservas=[to_reserva_response(db, reserva) for reserva in reservas]
    )


def get_reserva_by_user(
    db: Session,
    reserva_id: int,
    current_user
) -> ReservaResponseSchema:
    reserva = inscripcion_repository.get_by_id_and_usuario_id(
        db,
        reserva_id,
        current_user.id_usuario
    )

    if reserva is None:
        raise HTTPException(status_code=404, detail="Reserva no encontrada")

    return to_reserva_response(db, reserva)


def get_all_reservas_admin(db: Session) -> ReservaAdminListResponseSchema:
    reservas = inscripcion_repository.get_all(db)

    return ReservaAdminListResponseSchema(
        reservas=[to_reserva_admin_list_item(db, reserva) for reserva in reservas]
    )


def get_reserva_admin_by_id(db: Session, reserva_id: int) -> ReservaResponseSchema:
    reserva = inscripcion_repository.get_by_id(db, reserva_id)

    if reserva is None:
        raise HTTPException(status_code=404, detail="Reserva no encontrada")

    return to_reserva_response(db, reserva)


def update_reserva_pagado(db: Session, reserva_id: int, pagado: bool) -> ReservaMessageResponseSchema:
    reserva = inscripcion_repository.update(
        db,
        reserva_id,
        {"pagado": pagado}
    )

    if reserva is None:
        raise HTTPException(status_code=404, detail="Reserva no encontrada")

    return ReservaMessageResponseSchema(
        message="Estado de pago actualizado correctamente",
        reserva=to_reserva_response(db, reserva)
    )


def asignar_entrenamientos_reserva(
    db: Session,
    reserva_id: int,
    asignacion_data: ReservaEntrenamientosAsignarSchema
) -> ReservaEntrenamientosAsignadosResponseSchema:
    reserva = inscripcion_repository.get_by_id(db, reserva_id)

    if reserva is None:
        raise HTTPException(status_code=404, detail="Reserva no encontrada")

    entrenamientos_actuales = entrenamiento_repository.count_by_reserva_id(
        db,
        reserva.id_inscripcion
    )

    if entrenamientos_actuales >= reserva.numero_sesiones:
        raise HTTPException(
            status_code=400,
            detail="La reserva ya tiene la asignacion completa"
        )

    if entrenamientos_actuales > 0:
        raise HTTPException(
            status_code=400,
            detail="La reserva ya tiene entrenamientos asignados parcialmente"
        )

    if len(asignacion_data.entrenamientos) != reserva.numero_sesiones:
        raise HTTPException(
            status_code=400,
            detail=(
                "La lista debe tener exactamente "
                f"{reserva.numero_sesiones} entrenamientos"
            )
        )

    jugador = jugador_repository.get_by_usuario_id(db, reserva.id_usuario)

    for entrenamiento_data in asignacion_data.entrenamientos:
        if entrenamiento_data.hora_fin <= entrenamiento_data.hora_inicio:
            raise HTTPException(
                status_code=400,
                detail="La hora de fin debe ser posterior a la hora de inicio"
            )

        resolve_catalogos_entrenamiento(
            db,
            entrenamiento_data.id_entrenador,
            entrenamiento_data.id_ubicacion
        )

    entrenamientos_creados = []

    try:
        for entrenamiento_data in asignacion_data.entrenamientos:
            entrenador, ubicacion = resolve_catalogos_entrenamiento(
                db,
                entrenamiento_data.id_entrenador,
                entrenamiento_data.id_ubicacion
            )
            entrenamiento = entrenamiento_repository.create_without_commit(
                db,
                {
                    "id_entrenador": entrenador.id_entrenador,
                    "nombre_entrenador": entrenador.nombre,
                    "id_ubicacion": ubicacion.id_ubicacion,
                    "ubicacion": ubicacion.nombre,
                    "hora_inicio": entrenamiento_data.hora_inicio,
                    "hora_fin": entrenamiento_data.hora_fin,
                    "id_inscripcion": reserva.id_inscripcion,
                    "id_jugador": jugador.id_jugador if jugador is not None else None,
                    "id_usuario": reserva.id_usuario,
                    "id_reserva": reserva.id_inscripcion
                }
            )
            entrenamientos_creados.append(entrenamiento)

        db.commit()

        for entrenamiento in entrenamientos_creados:
            db.refresh(entrenamiento)

    except Exception:
        db.rollback()
        raise

    return ReservaEntrenamientosAsignadosResponseSchema(
        message="Entrenamientos asignados correctamente",
        entrenamientos=[
            to_entrenamiento_response(db, entrenamiento)
            for entrenamiento in entrenamientos_creados
        ]
    )


def get_entrenamientos_reserva_admin(
    db: Session,
    reserva_id: int
) -> EntrenamientoListResponseSchema:
    reserva = inscripcion_repository.get_by_id(db, reserva_id)

    if reserva is None:
        raise HTTPException(status_code=404, detail="Reserva no encontrada")

    entrenamientos = entrenamiento_repository.get_by_reserva_id(
        db,
        reserva.id_inscripcion
    )

    return EntrenamientoListResponseSchema(
        entrenamientos=[
            to_entrenamiento_response(db, entrenamiento)
            for entrenamiento in entrenamientos
        ]
    )


def reemplazar_entrenamientos_reserva_admin(
    db: Session,
    reserva_id: int,
    asignacion_data: ReservaEntrenamientosAsignarSchema
) -> ReservaEntrenamientosAsignadosResponseSchema:
    reserva = inscripcion_repository.get_by_id(db, reserva_id)

    if reserva is None:
        raise HTTPException(status_code=404, detail="Reserva no encontrada")

    if not reserva.pagado:
        raise HTTPException(
            status_code=400,
            detail="Solo se pueden editar entrenamientos de reservas pagadas"
        )

    if len(asignacion_data.entrenamientos) != reserva.numero_sesiones:
        raise HTTPException(
            status_code=400,
            detail=(
                "La lista final debe tener exactamente "
                f"{reserva.numero_sesiones} entrenamientos"
            )
        )

    jugador = jugador_repository.get_by_usuario_id(db, reserva.id_usuario)

    for entrenamiento_data in asignacion_data.entrenamientos:
        if entrenamiento_data.hora_fin <= entrenamiento_data.hora_inicio:
            raise HTTPException(
                status_code=400,
                detail="La hora de fin debe ser posterior a la hora de inicio"
            )

        resolve_catalogos_entrenamiento(
            db,
            entrenamiento_data.id_entrenador,
            entrenamiento_data.id_ubicacion
        )

    entrenamientos_creados = []

    try:
        entrenamiento_repository.delete_by_reserva_id_without_commit(
            db,
            reserva.id_inscripcion
        )

        for entrenamiento_data in asignacion_data.entrenamientos:
            entrenador, ubicacion = resolve_catalogos_entrenamiento(
                db,
                entrenamiento_data.id_entrenador,
                entrenamiento_data.id_ubicacion
            )
            entrenamiento = entrenamiento_repository.create_without_commit(
                db,
                {
                    "id_entrenador": entrenador.id_entrenador,
                    "nombre_entrenador": entrenador.nombre,
                    "id_ubicacion": ubicacion.id_ubicacion,
                    "ubicacion": ubicacion.nombre,
                    "hora_inicio": entrenamiento_data.hora_inicio,
                    "hora_fin": entrenamiento_data.hora_fin,
                    "id_inscripcion": reserva.id_inscripcion,
                    "id_jugador": jugador.id_jugador if jugador is not None else None,
                    "id_usuario": reserva.id_usuario,
                    "id_reserva": reserva.id_inscripcion
                }
            )
            entrenamientos_creados.append(entrenamiento)

        db.commit()

        for entrenamiento in entrenamientos_creados:
            db.refresh(entrenamiento)

    except Exception:
        db.rollback()
        raise

    return ReservaEntrenamientosAsignadosResponseSchema(
        message="Entrenamientos actualizados correctamente",
        entrenamientos=[
            to_entrenamiento_response(db, entrenamiento)
            for entrenamiento in entrenamientos_creados
        ]
    )
